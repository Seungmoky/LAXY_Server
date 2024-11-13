package laxy.server.service;

import laxy.server.dto.request.LoginRequest;
import laxy.server.dto.request.SignUpRequest;
import laxy.server.dto.request.UpdateMemberRequest;
import laxy.server.dto.response.LoginResponse;
import laxy.server.entity.Member;
import laxy.server.exception.ApiException;
import laxy.server.exception.ErrorType;
import laxy.server.repository.MemberRepository;
import laxy.server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorType.EMAIL_ALREADY_EXISTS);
        }

        LocalDate birthDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthDate = LocalDate.parse(request.getBirth(), formatter);
        } catch (DateTimeParseException e) {
            throw new ApiException(ErrorType.INVALID_DATE_FORMAT, "yyyy-MM-dd 형식으로 보내주세요.");
        }

        Member member = new Member(
                request.getEmail(),
                request.getPassword(), // 비밀번호 암호화 없이 그대로 저장
                request.getName(),
                birthDate.atStartOfDay(),
                request.getGender()
        );

        memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest request) {
        // 이메일로 회원 정보 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        // 비밀번호가 일치하지 않으면 예외 발생
        if (!request.getPassword().equals(member.getPassword())) {
            throw new ApiException(ErrorType.INVALID_PASSWORD);
        }

        // Access Token과 Refresh Token 생성
        String accessToken = JwtUtil.generateAccessToken(member.getId());
        String refreshToken = JwtUtil.generateRefreshToken(member.getId());

        // LoginResponse 객체를 생성하여 반환
        return new LoginResponse(accessToken, refreshToken, member.getName(), member.getEmail());
    }

    @Transactional
    public void updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        if (request.getName() != null && !request.getName().isEmpty()) {
            member.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            member.setPassword(request.getPassword()); // 비밀번호 암호화 없이 그대로 업데이트
        }
        if (request.getBirth() != null && !request.getBirth().isEmpty()) {
            LocalDate birthDate;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birthDate = LocalDate.parse(request.getBirth(), formatter);
            } catch (DateTimeParseException e) {
                throw new ApiException(ErrorType.INVALID_DATE_FORMAT, "yyyy-MM-dd 형식으로 보내주세요.");
            }
            member.setBirth(birthDate.atStartOfDay());
        }
        if (request.getGender() != null && !request.getGender().isEmpty()) {
            member.setGender(request.getGender());
        }
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorType.MEMBER_NOT_FOUND));

        memberRepository.delete(member);
    }

    public String refreshAccessToken(Long memberId) {
        return JwtUtil.generateAccessToken(memberId);
    }
}
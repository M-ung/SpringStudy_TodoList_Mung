package com.example.todolist.service.member;

import com.example.todolist.common.exception.CustomException;
import com.example.todolist.common.response.ErrorCode;
import com.example.todolist.dto.member.MemberRequestDTO;
import com.example.todolist.dto.member.MemberResponseDTO;
import com.example.todolist.entity.member.Member;
import com.example.todolist.repository.member.MemberRepository;
import com.example.todolist.repository.todo.TodoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;
    @Override
    @Transactional
    public MemberResponseDTO.MemberJoinDTO join(MemberRequestDTO.MemberJoinDTO memberJoinDTO) {
        try {
            log.info("[MemberServiceImpl] join");
            Member member = memberJoinDTO.toEntity();

            Optional<Member> optionalFindMember = memberRepository.findByMemberEmail(member.getMemberEmail());

            if(optionalFindMember.isPresent()) {
                // 중복 이메일 발생
                log.info("[ERROR] 중복 이메일 입니다.");
                throw new CustomException(ErrorCode.EMAIL_EXIST);
            }

            memberRepository.save(member); // 중복 이메일이 없다면 DB에 저장하기.

            return new MemberResponseDTO.MemberJoinDTO(member);
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl join");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl join");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl join : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public MemberResponseDTO.MemberLoginDTO login(MemberRequestDTO.MemberLoginDTO memberLoginDTO) {
        try {
            log.info("[MemberServiceImpl] login");
            Optional<Member> optionalFindMember = memberRepository.findByMemberEmail(memberLoginDTO.getMemberEmail()); // DB에서 회원 조회

            if(optionalFindMember.isEmpty()) {
                // 존재하지 않은 이메일
                log.info("[ERROR] 존재하지 않은 이메일 입니다.");
                throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            }
            else if(!Objects.equals(memberLoginDTO.getMemberPassword(), optionalFindMember.get().getMemberPassword())) {
                // 틀린 비밀번호
                throw new CustomException(ErrorCode.PASSWORD_NOT_FOUND);
            }

            return new MemberResponseDTO.MemberLoginDTO(optionalFindMember.get());
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl login");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl login");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl login : " + e.getMessage());
        }
    }

    @Override
    public MemberResponseDTO.MemberLogoutDTO logout(HttpServletRequest request, Long memberId) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // 세션 무효화
            }

            Optional<Member> optionalFindMember = memberRepository.findById(memberId); // DB에서 회원 조회

            if(optionalFindMember.isEmpty()) {
                // 존재하지 않은 이메일
                log.info("[ERROR] 존재하지 않은 회원 입니다.");
                throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            }
            return new MemberResponseDTO.MemberLogoutDTO(optionalFindMember.get());
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl logout");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl logout");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl logout : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String delete(Long memberId) {
        try {
            log.info("[MemberServiceImpl] delete");
            Optional<Member> optionalFindMember = memberRepository.findById(memberId);

            if(optionalFindMember.isEmpty()) {
                // 존재하지 않은 이메일
                log.info("[ERROR] 존재하지 않은 회원 입니다.");
                throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            }

            todoRepository.deleteByMemberId(memberId);
            memberRepository.deleteById(memberId); // DB에서 회원 삭제

            return "SUCCESS";
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl delete");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl delete");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl delete : " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public MemberResponseDTO.MemberUpdateDTO update(Long memberId, MemberRequestDTO.MemberUpdateDTO memberUpdateDTO) {
        try {
            log.info("[MemberServiceImpl] update");
            Optional<Member> optionalFindMember = memberRepository.findById(memberId); // DB에서 회원 조회

            if(!optionalFindMember.isPresent()) {
                // 존재하지 않은 이메일
                log.info("[ERROR] 존재하지 않은 이메일 입니다.");
                throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            }

            // 회원 수정 과정 진행
            optionalFindMember.get().memberUpdate(memberUpdateDTO);

            return new MemberResponseDTO.MemberUpdateDTO(optionalFindMember.get());
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl update");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl update");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl update : " + e.getMessage());
        }
    }

    @Override
    public MemberResponseDTO.MemberFindOneDTO findOne(Long memberId) {
        try {
            log.info("[MemberServiceImpl] findOne");

            Optional<Member> optionalFindMember = memberRepository.findById(memberId); // DB에서 회원 조회

            if(optionalFindMember.isEmpty()) {
                // 존재하지 않은 이메일
                log.info("[ERROR] 존재하지 않은 회원 입니다.");
                throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
            }

            return new MemberResponseDTO.MemberFindOneDTO(optionalFindMember.get());
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl findOne");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl findOne");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl findOne : " + e.getMessage());
        }
    }

    @Override
    public MemberResponseDTO.MemberFindAllDTO findAll() {
        try {
            log.info("[MemberServiceImpl] findAll");
            List<Member> memberList = memberRepository.findAll();

            List<MemberResponseDTO.MemberFindOneDTO> memberFindOneDTOList = memberList.stream()
                    .map(MemberResponseDTO.MemberFindOneDTO::new)
                    .collect(Collectors.toList());

            return new MemberResponseDTO.MemberFindAllDTO(memberFindOneDTOList);
        } catch (CustomException ce){
            log.info("[CustomException] MemberServiceImpl findAll");
            throw ce;
        } catch (Exception e){
            log.info("[Exception500] MemberServiceImpl findAll");
            throw new CustomException(ErrorCode.SERVER_ERROR, "MemberServiceImpl findAll : " + e.getMessage());
        }
    }
}

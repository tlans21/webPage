package HomePage.service;

import HomePage.domain.Member;
import HomePage.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    public void validateDuplicateMember(Member member) {
        memberRepository.findByName(member.getName())
                        .ifPresent(m -> {
                            throw new IllegalStateException("이미 존재하는 이름입니다.");
                        });

        memberRepository.findByEmail(member.getEmail())
                        .ifPresent(m -> {
                            throw new IllegalStateException("이미 존재하는 이메일입니다.");
                        });


    }

    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId){
        return memberRepository.findById(memberId);
    }

    public Optional<Member> authenticateMember(String email, String password){
        Optional<Member> member = memberRepository.findByEmail(email);
        System.out.println(member.get());
        System.out.println(member.get().getEmail());
        if(!member.isPresent()){
            return Optional.empty();
        }
        System.out.println(member.get().getPassword());
        System.out.println(password);
        if(!password.equals(member.get().getPassword())){
            return Optional.empty();
        }

        return member.stream().findAny();
    }
}

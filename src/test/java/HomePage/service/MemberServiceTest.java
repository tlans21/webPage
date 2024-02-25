package HomePage.service;

import HomePage.domain.Member;
import HomePage.repository.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberServiceTest {
    MemberService memberService;
    MemoryMemberRepository memberRepository;
    @BeforeEach
    public void beforeEach() {
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }
    @AfterEach
    public void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void join() {
        //given
        Member member = new Member();
        member.setName("simun");
        //when
        Long saveId = memberService.join(member);

        //then
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());

    }

    @Test
    void joinException() {
        //given
        Member member1 = new Member();

        member1.setName("simun");
        member1.setEmail("tlans20@naver.com");
        Member member2 = new Member();
        member2.setName("simun");
        member2.setEmail("tlans21@naver.com");
        //when

        Member member3 = new Member();
        member3.setName("simun100");
        member3.setEmail("tlans22@naver.com");

        Member member4 = new Member();
        member4.setName("simun200");
        member4.setEmail("tlans22@naver.com");

        memberService.join(member1);
        IllegalStateException e1 = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e1.getMessage()).isEqualTo("이미 존재하는 이름입니다.");

        memberService.join(member3);
        IllegalStateException e2 = assertThrows(IllegalStateException.class, () -> memberService.join(member4));
        assertThat(e2.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
        //then
    }

    @Test
    void findOne() {
        Member member = new Member();
        member.setName("simun");
        memberService.join(member);
        Member member1 = memberService.findOne(member.getId()).get();
        assertThat(member.getName()).isEqualTo(member1.getName());
    }
    @Test
    public void 로그인(){
        Member registeredMember = new Member();
        registeredMember.setName("simunss");
        registeredMember.setEmail("simun@naver.com");
        registeredMember.setPassword("1234");
        memberService.join(registeredMember);

        Member member = new Member();
        member.setName("simunss");
        member.setEmail("simun@naver.com");
        member.setPassword("1234");
        Member result = memberService.authenticateMember(member.getEmail(), member.getPassword()).get();
        assertThat(member.getName()).isEqualTo(result.getName());
    }
}
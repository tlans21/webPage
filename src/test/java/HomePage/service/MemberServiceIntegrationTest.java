package HomePage.service;

import HomePage.domain.Member;
import HomePage.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional

class MemberServiceIntegrationTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        //Given
        Member member = new Member();
        member.setName("hello");
        //When
        Long saveId = memberService.join(member);
        //Then
        Member findMember = memberRepository.findById(saveId).get();
        assertEquals(member.getName(), findMember.getName());
    }
    @Test
    public void 중복_회원_예외() throws Exception {
        //Given
        // 이름만 중복인 경우
        Member member1 = new Member();
        member1.setName("spring");
        member1.setEmail("tlans20@naver.com");
        Member member2 = new Member();
        member2.setName("spring");
        member2.setEmail("tlans21@naver.com");

        // 이메일이 중복인 경우
        Member member3 = new Member();
        member3.setName("simun1");
        member3.setEmail("tlans21@naver.com");
        Member member4 = new Member();
        member4.setName("simun2");
        member4.setEmail("tlans21@naver.com");

        //When
        memberService.join(member1);
        IllegalStateException e1 = assertThrows(IllegalStateException.class,
                () -> memberService.join(member2));//예외가 발생해야 한다.
        assertThat(e1.getMessage()).isEqualTo("이미 존재하는 이름입니다.");
        memberService.join(member3);
        IllegalStateException e2 = assertThrows(IllegalStateException.class,
                () -> memberService.join(member4));//예외가 발생해야 한다.
        assertThat(e2.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }
}
package HomePage.repository;

import HomePage.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();


    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setName("simun");
        member.setPassword("1234");
        member.setEmail("tlans21@naver.com");
        member.setPhoneNumber("010-1234-5678");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();

        assertThat(member).isEqualTo(result);
    }

    @Test
    public void findById() {
        Member member = new Member();
        member.setName("simun");
        member.setPassword("1234");
        member.setEmail("tlans21@naver.com");
        member.setPhoneNumber("010-1234-5678");

        Member saveMember = repository.save(member);
        Member targetMember = repository.findById(saveMember.getId()).get();
        assertThat(saveMember).isEqualTo(targetMember);
    }

    @Test
    public void findByEmail() {
        Member member = new Member();
        member.setName("simun");
        member.setPassword("1234");
        member.setEmail("tlans21@naver.com");
        member.setPhoneNumber("010-1234-5678");

        Member saveMember = repository.save(member);
        Member targetMember = repository.findByEmail(saveMember.getEmail()).get();
        assertThat(saveMember).isEqualTo(targetMember);
    }

    @Test
    public void findByPhoneNumber() {
        Member member = new Member();
        member.setName("simun");
        member.setPassword("1234");
        member.setEmail("tlans21@naver.com");
        member.setPhoneNumber("010-1234-5678");

        Member saveMember = repository.save(member);
        Member targetMember = repository.findByPhoneNumber(saveMember.getPhoneNumber()).get();
        assertThat(saveMember).isEqualTo(targetMember);
    }

    @Test
    public void findById_ExceptionOccurs(){

    }
}
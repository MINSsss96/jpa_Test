package com.my.jpaTest.quiz;

import com.my.jpaTest.Dto.Gender;
import com.my.jpaTest.entity.Users;
import com.my.jpaTest.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class QuizTest {
    @Autowired
    UsersRepository repository;

    @Test
    @DisplayName("Give/When/Then 으로 테스트 하기")
    void assertThatTest() {
        //신규데이터 추가 테스트
        //Given
        Users jin = Users.builder()
                .name("안유진")
                .email("jin@korea.com")
                .gender(Gender.Female)
                .likeColor("Red")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //When
        repository.save(jin);
        //Then
        // 이름으로 검색한 결과와 jin 이랑 같으면 성공
        Users result = repository.findByName("안유진").get(0);
        //검사
        Assertions.assertThat(result.getEmail()).isEqualTo(jin.getEmail());
    }

    @Test
    @DisplayName("문제1")
    void findByGenderAndNameContainingOrGenderAndNameContaining() {
        List<Users> users = repository.findByGenderAndNameContainingOrGenderAndNameContaining(Gender.Female, "w", Gender.Female, "m");
        users.forEach(System.out::println);
    }

    @Test
    @DisplayName("문제2")
    void findByEmailLike() {
        repository.findByEmailLike("%net")
                .forEach(x -> System.out.println(x));
    }

    @Test
    @DisplayName("문제3")
    void findByCreatedAtBetweenAndNameStartingWith(){
        // 한달 이전의 기준일 설정
        LocalDate baseDate = LocalDate.now().minusMonths(1L);
        System.out.println(baseDate);
        // 한달 전 날에다 시분초를 붙인다.
        LocalDateTime start = baseDate.atTime(0, 0,0);
        LocalDateTime end = LocalDateTime.now();
        repository.findByCreatedAtBetweenAndNameStartingWith(start, end, "J")
                .forEach(x-> System.out.println(x));
    }

    @Test
    @DisplayName("가장 최근 생성된 10건의 ID, 이름, 성별, 생성일 출력")
    void testFindTop10RecentUsers() {
        List<Users> recentUsers = repository.findTop10ByOrderByCreatedAtDesc();

        assertNotNull(recentUsers);
        assertTrue(recentUsers.size() <= 10);

        System.out.println("==== 최근 생성된 사용자 10건 ====");
        for (Users user : recentUsers) {
            System.out.printf("ID: %d, 이름: %s, 성별: %s, 생성일: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getGender(),
                    user.getCreatedAt()
            );
        }
    }

    @Test
    @DisplayName("Red를 좋아하는 남성의 이메일 아이디 부분만 출력")
    void findByEmailContainingAndGenderOrderByCreatedAtDesc() {
        List<Users> users = repository.findByGenderAndLikeColor(Gender.Male, "Red");

        assertNotNull(users);

        System.out.println("=== Red를 좋아하는 남성 이메일 아이디 목록 ===");
        for (Users user : users) {
            String email = user.getEmail();
            String emailId = email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : "잘못된 이메일";
            System.out.println(emailId);
        }
    }

    @Test
    @DisplayName("갱신일이 생성일보다 이전인 잘못된 데이터 출력 (@Query 없이)")
    void testFindInvalidUsersWithoutQuery() {
        List<Users> allUsers = repository.findAll();

        List<Users> invalidUsers = allUsers.stream()
                .filter(user -> {
                    LocalDateTime createdAt = user.getCreatedAt();
                    LocalDateTime updatedAt = user.getUpdatedAt();
                    return createdAt != null && updatedAt != null && updatedAt.isBefore(createdAt);
                })
                .toList();

        assertNotNull(invalidUsers);

        System.out.println("=== 잘못된 사용자 데이터 (updatedAt < createdAt) ===");
        for (Users user : invalidUsers) {
            System.out.printf("ID: %d, 이름: %s, 생성일: %s, 갱신일: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
    }


    @Test
    @DisplayName("이메일에 'edu'가 포함된 여성 데이터 출력 (최신순)")
    void testFindFemaleUsersWithEduEmail() {
        List<Users> users = repository.findByEmailContainingAndGenderOrderByCreatedAtDesc("edu", Gender.Female);

        assertNotNull(users);

        System.out.println("=== 'edu' 이메일을 가진 여성 사용자 (최신순) ===");
        for (Users user : users) {
            System.out.printf("ID: %d, 이름: %s, 이메일: %s, 생성일: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt()
            );
        }
    }

    @Test
    @DisplayName("좋아하는 색상 'Pink'인 사용자 - 색상 오름차순, 이름 내림차순 정렬")
    void testFindUsersByFavoriteColorSorted() {
        // 오름차순(likeColor), 내림차순(name)
        Sort sort = Sort.by(Sort.Order.asc("likeColor"), Sort.Order.desc("name"));

        List<Users> users = repository.findByLikeColor("Pink", sort);

        assertNotNull(users);

        System.out.println("=== 'Pink'를 좋아하는 사용자 (색상 ASC, 이름 DESC) ===");
        for (Users user : users) {
            System.out.printf("ID: %d, 이름: %s, 색상: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getLikeColor()
            );
        }
    }

    @Test
    @DisplayName("전체 자료를 최신순으로 정렬 후 페이징 처리 (10건씩, 1번째 페이지)")
    void testPagedUsersByCreatedAtDesc() {
        // PageRequest.of(pageNumber, pageSize, Sort)
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Users> page = repository.findAll(pageable);

        List<Users> users = page.getContent();

        assertNotNull(users);

        System.out.println("=== 최신순 1번째 페이지 사용자 목록 (1페이지당 10건) ===");
        for (Users user : users) {
            System.out.printf("ID: %d, 이름: %s, 생성일: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getCreatedAt()
            );
        }

        System.out.printf("총 페이지 수: %d, 총 요소 수: %d, 현재 페이지 번호: %d%n",
                page.getTotalPages(), page.getTotalElements(), page.getNumber() + 1
        );
    }


    @Test
    @DisplayName("남성 사용자 - ID 내림차순 정렬, 2번째 페이지 (3건씩)")
    void testFindMaleUsersPagedAndSorted() {
        // 2번째 페이지 → 인덱스는 1 (0부터 시작)
        Pageable pageable = PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "id"));

        Page<Users> page = repository.findByGender(Gender.Male, pageable);

        List<Users> users = page.getContent();

        assertNotNull(users);

        System.out.println("=== 남성 사용자 (ID DESC, 2번째 페이지, 3건씩) ===");
        for (Users user : users) {
            System.out.printf("ID: %d, 이름: %s, 성별: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getGender()
            );
        }

        System.out.printf("총 페이지 수: %d, 전체 남성 수: %d, 현재 페이지: %d%n",
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber() + 1 // 사람이 보는 페이지 번호 (1-based)
        );
    }

    @Test
    @DisplayName("지난달 생성된 사용자 데이터 출력")
    void testFindUsersCreatedLastMonth() {
        LocalDateTime startOfLastMonth = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime endOfLastMonth = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay()
                .minusNanos(1);

        List<Users> users = repository.findByCreatedAtBetween(startOfLastMonth, endOfLastMonth);

        assertNotNull(users);

        System.out.println("=== 지난달 생성된 사용자 목록 ===");
        for (Users user : users) {
            System.out.printf("ID: %d, 이름: %s, 생성일: %s%n",
                    user.getId(),
                    user.getName(),
                    user.getCreatedAt()
            );
        }
    }

}

package com.my.jpaTest.repository;

import com.my.jpaTest.Dto.Gender;
import com.my.jpaTest.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {
    // 쿼리메서드 생성
    // 이름으로 검색하기
    // Select * form users where name ='장원영';
    // findByName(String searchName);
    List<Users> findByName(String searchName);

    // 2. 상위 3개 같은 색상 정보 찾기
    // select * from users where color='pink' limit 3;
    List<Users> findTop3ByLikeColor(String color);

    // 3. 성별이 여자이고 좋아하는 색상이 Red 인 자료
    // select * from users where gender='female' and like_color='Red';
    List<Users> findByGenderAndLikeColor(Gender gender, String color);

    // 4. 범위 검색(날짜, 시간)
    // 어제 이후 생성된 모든 자료 검색
    // select * from users where created_at >='어제'
    List<Users> findByCreatedAtAfter(LocalDateTime searchDate);

    // 5. 최근 1개월 자료 검색하기
    List<Users> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 6. 좋아하는 색상이 Pink, Red 인 모든 자료 출력
    // select * from Users where like_color in ('Red','Pink');
    // In 구문에서는 리스트를 인자로 준다.
    List<Users> findByLikeColorIn(List<String> colors);

    // 7. id가 91번이 이상인 자료 찾아보기
    // >= : GreaterThanEqual, <= : LessThanEqual
    // > : After , < : Before
    // null 값 비교 : Null or IsNotNull
    List<Users> findByIdGreaterThanEqual(Long id);

    // 8. 문자열 관련 메서드 함수
    // StartingWith : 주어진 문자열로 시작하는 데이터
    // EndingWith : 주어진 문자열로 끝나는 데이터
    // Contains : 포함된 자료
    // Like : 사용시 넘겨주는 인자 값 양쪽에 %를 붙여주어야함
    // 8.1. 이름이 D로 시작하는 데이터 전체 출력
    // select * from users where name like 'D%';
    List<Users> findByNameStartingWith(String x);
    // 8.2. 이름이 S로 끝나는 데이터 전체 출력
    // select * from users where name like '%S';
    List<Users> findByNameEndingWith(String x);
    // 8.3. 이메일이 org를 포함하는 데이터 출력(Contains / Like)
    // // select * from users where email like '%org%';
    List<Users> findByEmailContains(String x);
    List<Users> findByEmailLike(String x);

    // 9. 정렬
    // id : 1~10까지 이름의 내림차순으로 정렬
    // select * from users where id between 1 to 10 order by name desc;
    List<Users> findByIdBetweenOrderByNameDesc(Long start, Long end);

    // 퀴즈
    // Orange 색상 중 Gender에 오름차순, CreatedAt에 내림차순 후 상위 10개 검색
    // select * from users where like_color='orange' order by gender asc, createdAt desc
    List<Users> findTop10ByLikeColorOrderByGenderAscCreatedAtDesc(String color);

    //10. sort 사용하기
    List<Users> findByLikeColor(String color, Sort sort);

    // quizTest1
    // 문제 1. 여성의 이름 중 "w"또는 "m"을 포함하는 자료를 검색하시오.
    List<Users> findByGenderAndNameContainingOrGenderAndNameContaining(Gender g1, String name1, Gender g2, String name2);

    // quizTest2
    // 문제 2. 이메일에 net을 포함하는 데이터 건수를 출력하시오.
    // List<Users> findByEmailLike(String x);

    // quizTest3
    // 문제 3. 가장 최근 한달이내에 업데이트된 자료 중 이름 첫자가 "J"인 자료를 출력하시오.
    List<Users> findByCreatedAtBetweenAndNameStartingWith(LocalDateTime start, LocalDateTime end,String x);

    // quizTest4
    // 문제 4. 가장 최근 생성된 자료 10건을 ID, 이름, 성별, 생성일 만 출력하시오.
    List<Users> findTop10ByOrderByCreatedAtDesc();

    // quizTest5
    // 문제5. "Red"를 좋아하는 남성 이메일 계정 중 사이트를 제외한 계정만 출력하시오.
    // (예, apenley2@tripod.com  → apenley2)
//    List<Users> findByLikeColorAndGenderAndEmailLike(String LikeColor, Gender gender, String email);
//    List<Users> findByGenderAndLikeColor(Gender gender, String likeColor);

    // quizTest6
    // 문제 6. 갱신일이 생성일 이전인 잘못된 데이터를 출력하시오.
    List<Users> findByUpdatedAtBeforeAndCreatedAtAfter(LocalDateTime updatedAt, LocalDateTime createdAt);

    // quizTest7
    // 문제 7. 이메일에 edu를 갖는 여성 데이터를 가장 최근 데이터부터 보이도록 출력하시오.
    List<Users> findByEmailContainingAndGenderOrderByCreatedAtDesc(String keyword, Gender gender);

    // quizTest7
    // 문제 8. 좋아하는 색상(Pink)별로 오름차순 정렬하고 같은 색상 데이터는 이름의 내림차순으로 출력하시오.
//    List<Users> findByLikeColor(String LikeColor, Sort sort);

    // quizTest10
    // 문제10. 남성 자료를 ID의 내림차순으로 정렬한 후 한페이당 3건을 출력하되 그 중 2번째 페이지 자료를  출력하시오.
    Page<Users> findByGender(Gender gender, Pageable pageable);

    // quizTest11
    // 문제11. 지난달의 모든 자료를 검색하여 출력하시오.
//    List<Users> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}

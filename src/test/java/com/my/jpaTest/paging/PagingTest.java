package com.my.jpaTest.paging;

import com.my.jpaTest.entity.Users;
import com.my.jpaTest.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PagingTest {
    @Autowired
    UsersRepository usersRepository;

    @Test
    @DisplayName("Paging Test")
    void pagingTest(){
        System.out.println("페이지 = 0 , 페이지당 리스트 수 : 5");
        usersRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("name")))
        ).getContent()
                .forEach(x-> System.out.println(x));
    }

    // 전체 검색 후에 정렬을 수행한 페이징 처리
    @Test
    @DisplayName("전체 검색 후에 정렬을 수행한 페이징 처리")
    void findAllPaging(){
        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(0, 5, sort);
//        Pageable pageable = PageRequest.of(0,5);
        Page<Users> result = usersRepository.findAll(pageable);
        result.getContent().forEach(x-> System.out.println(x));

        // 각종 페이징 정보
        // 1. 총 페이지 수
        System.out.println("Total pages : " + result.getTotalPages());
        // 2. 총 게시물 수
        System.out.println("Total Count : " + result.getTotalElements());
        // 3. 총 페이지 번호
        System.out.println("Current Page Number : " + result.getNumber());
        // 4. 현재 페이지의 목록 수
        System.out.println("Page Size : " + result.getSize());
        // 5. 다음 페이지의 존재 여부
        System.out.println("Has Next Page? : " + result.hasNext());
        // 6. 시작페이지의 존재 여부
        System.out.println("is First Page : " + result.isFirst());

    }
}

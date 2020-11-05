## Spring Batch Study

### 방향

1. 스프링 배치를 반복적으로 활용하여 숙달
2. 스프링 배치 관리(수행된 리스트 및 실패 했을시 장애포인트를 찾아 처리할 수 있도록)

### 데이터

- 

### Job 시나리오

1. 가공되지 않은 library.csv data → tmp_library Table에 저장
2. tmp_library Table 에서 관계를 추가하여 각각의 테이블(library, big_local, small_local )에 추가
# 분산 트랜잭션 셀프 학습

> 간단한 주문 시스템을 직접 만들며 **2PC, TCC, SAGA**로 분산 트랜잭션을 익히는 저장소입니다.
>
> 참고 강의: [인프런 「주문시스템으로 알아보는 분산트랜잭션」](https://www.inflearn.com/course/%EC%A3%BC%EB%AC%B8%EC%8B%9C%EC%8A%A4%ED%85%9C%EC%9C%BC%EB%A1%9C-%EC%95%8C%EC%95%84%EB%B3%B4%EB%8A%94-%EB%B6%84%EC%82%B0%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98?cid=337778)의 커리큘럼 흐름을 기준으로, 강의 없이 혼자 진행합니다.

---

## 학습 목표

- 모놀리식에서 MSA로 바꿀 때 **트랜잭션 정합성이 왜 깨지는지** 직접 재현한다.
- 동시성 문제를 **멱등 키와 락**으로 제어한다.
- **2PC, TCC, SAGA(Orchestration, Choreography)** 의 구조와 장단점을 설명하고, TCC와 SAGA는 직접 구현한다.
- 각 패턴에서 **실패, 타임아웃, 재시도, 중복** 상황을 어떻게 다룰지 스스로 판단 기준을 세운다.

## 도메인

결제 연동 없이 세 가지 도메인만 사용합니다.

| 도메인 | 책임 |
|---|---|
| 주문(Order) | 주문 생성, 상태 관리 (생성 → 완료 / 실패) |
| 재고(Product) | 상품 재고 차감, 복구 |
| 포인트(Point) | 사용자 포인트 차감, 복구 |

**주문 흐름:** 주문 요청 → 재고 차감 → 포인트 차감 → 주문 완료. 중간에 하나라도 실패하면 **전체가 없던 일**이 되어야 합니다.

## 기술 스택 (예정)

- Java 17+, Spring Boot 3, Spring Data JPA
- MySQL (서비스별 DB 분리), Redis (분산 락)
- Docker Compose
- Kafka (7단계 Choreography)
- 테스트: JUnit 5, 동시성 테스트(`ExecutorService`, `CountDownLatch`)

---

## 로드맵

| 단계 | 주제 | 문서 | 완료 |
|---|---|---|---|
| 1 | 모놀리식 주문 시스템과 `@Transactional` | [01-monolithic](docs/01-monolithic.md) | [ ] |
| 2 | 중복 주문 방지: 멱등 키와 락 | [02-idempotency-lock](docs/02-idempotency-lock.md) | [ ] |
| 3 | MSA로 분리하고 정합성 문제 재현 | [03-msa-split](docs/03-msa-split.md) | [ ] |
| 4 | 2PC 개념 정리 | [04-2pc](docs/04-2pc.md) | [ ] |
| 5 | TCC 구현 | [05-tcc](docs/05-tcc.md) | [ ] |
| 6 | SAGA Orchestration 구현 | [06-saga-orchestration](docs/06-saga-orchestration.md) | [ ] |
| 7 | SAGA Choreography 구현 | [07-saga-choreography](docs/07-saga-choreography.md) | [ ] |

각 단계 문서에는 **요구사항, 완료 기준, 직접 재현할 실패 시나리오, 정리할 질문**이 있습니다.

## 공부 방법

1. **문제를 먼저 겪는다.** 해결책을 구현하기 전에, 그 해결책이 없으면 무엇이 깨지는지 테스트로 먼저 재현한다.
2. **완료 기준은 테스트로 확인한다.** "될 것 같다"가 아니라 시나리오 테스트가 통과해야 다음 단계로 넘어간다.
3. **단계마다 범위를 지킨다.** Outbox, 이벤트 소싱, 분산 추적 같은 확장 주제는 [더 나아가기](#더-나아가기)에 적어 두고 나중에 한다.
4. **단계가 끝나면 문서의 질문에 답을 쓴다.** 답은 각 문서 하단 `회고` 섹션에 남긴다.
5. **막히면** 참고 자료를 먼저 보고, 그래도 판단이 서지 않는 부분만 강의로 확인한다.

## 패턴 비교 (완성하면서 채우기)

| 항목 | 로컬 트랜잭션 | 2PC | TCC | SAGA Orchestration | SAGA Choreography |
|---|---|---|---|---|---|
| 일관성 | 강한 일관성 | | | | |
| 격리성 | | | | | |
| 성능 / 가용성 | | | | | |
| 구현 복잡도 | | | | | |
| 실패 처리 방식 | 롤백 | | | | |
| 적합한 상황 | | | | | |

---

## 참고 자료

- [microservices.io: Saga 패턴](https://microservices.io/patterns/data/saga.html)
- [microservices.io: Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html)
- Chris Richardson, 『마이크로서비스 패턴』 4장 (트랜잭션 관리: 사가)
- Martin Kleppmann, 『데이터 중심 애플리케이션 설계』 9장 (일관성과 합의, 2PC)
- Atomikos: Try-Confirm-Cancel 패턴 소개 자료

## 더 나아가기

- [ ] Transactional Outbox로 이벤트 발행 신뢰성 보장
- [ ] 메시지 소비 멱등성 (Inbox 테이블)
- [ ] 보상 트랜잭션 실패 시 재시도와 수동 처리 큐
- [ ] 분산 추적 (Micrometer Tracing, Zipkin)

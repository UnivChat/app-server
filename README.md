# app-server
> 💻 UnivChat의 백엔드 Repository 💻 

## 📌 Commit Convention
```
type: subject #issue번호

body
```
> type: 대문자로 작성 </br>
subejct: 간결하되 자세하게 작성 </br>
body: 구체적인 내용

</br>

|type|description|
|:---:|:---:|
|FEAT|새로운 기능 추가|
|FIX|버그 수정|
|MOD|코드 수정, 로직에 영향|
|CHORE|코드 수정, 로직에 영향 없음|
|REFACTOR|코드 리팩토링|
|TEST|테스트 코드 추가|



</br></br>

## 📌 Git Convention
### branch
|branch|description|
|:---:|:---:|
|main|기본 브랜치, 배포 가능한 브랜치|
|develop|main에 merge 전 통합하는 브랜치|
|feature/#issue번호|이슈 개발 브랜치|

</br>

### git flow
```
1. 작업할 issue 생성
2. feature/#이슈번호 로 브랜치 생성 -> 작업
3. 커밋 메시지 작성 시 뒤에 #이슈번호 붙이기
4. 작업 완료 했다면 push
5. develop 브랜치에 PR(Pull Request) 날리기
6. 팀원 코드 리뷰 후 develop에 머지
7. 테스트 진행 후 main에 머지
```

## 선한 건강정보고속도로 백엔드

### 프로젝트 개요
- `sunhan_myhealthway_backend`는 병원 시스템을 지원하는 백엔드 애플리케이션입니다.
- 이 프로젝트는 환자 관리, 사용자 인증, 관리자 설정 및 외부 웹뷰어 연동 기능을 제공합니다.
- 환자 정보를 조회하고, 사용자와 관리자가 안전하게 로그인하며, 관리자가 시스템 설정을 동적으로 변경할 수 있도록 설계되었습니다.

### 주요 기능

* **사용자 인증 및 권한 관리**:
- **일반 사용자 로그인**: 사용자 ID를 기반으로 로그인하며, 관리자가 아닌 경우 비밀번호 입력 없이 로그인합니다.
- **관리자 로그인**: 관리자 계정은 추가로 비밀번호를 입력해야 로그인이 가능합니다.
- **토큰 유효성 검사**: JWT(JSON Web Token)를 사용하여 사용자의 인증 상태와 토큰 유효성을 검증합니다.
- **부서 목록 조회**: 모든 부서의 목록을 조회합니다.

* **환자 관리**:
- `clncCnfrmFlag` (진료 확인 플래그) 값에 따라 환자 정보를 조회합니다.
- 로그인한 사용자의 부서 코드에 해당하는 환자 목록만 조회할 수 있습니다.

* **웹뷰어 연동**:
- 외부 웹뷰어를 안전하게 열 수 있도록 인증 및 URL을 생성합니다.
- 접근 권한이 있는 사용자에게 웹뷰어 접속 URL을 반환합니다.

* **관리자 설정**:
- **설정 조회**: 현재 설정된 URL, Client ID, Client Secret 등의 관리자 설정 값을 조회합니다.
- **설정 업데이트**: URL, Client ID, Client Secret, Utilization Service No, Institution Code, Seed Key를 동적으로 업데이트할 수 있습니다.
- **환자 테스트 요청**: 특정 주민등록번호와 사용자 ID를 이용해 환자 테스트 요청을 수행할 수 있습니다.

### 기술 스택

- **백엔드**: Java, Spring Boot
- **보안**: JWT (JSON Web Token)
- **API 문서**: RESTful API

### API 엔드포인트

#### 1. 인증 (`/api/auth`)

| HTTP 메소드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- |
| `POST` | `/login` | 사용자가 로그인합니다. |
| `POST` | `/validate` | 토큰의 유효성을 검사합니다. |
| `GET` | `/departments` | 등록된 모든 부서를 조회합니다. |

---

#### 2. 웹뷰어 (`/api/webviewer`)

| HTTP 메소드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- |
| `POST` | `/open` | 웹뷰어 접속을 위한 URL을 생성합니다. |

---

#### 3. 관리자 (`/api/admin`)

| HTTP 메소드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- |
| `POST` | `/login` | 관리자가 로그인합니다. |
| `GET` | `/settings` | 관리자 설정을 조회합니다. |
| `POST` | `/settings/url` | URL을 업데이트합니다. |
| `POST` | `/settings/client-id` | Client ID를 업데이트합니다. |
| `POST` | `/settings/client-secret` | Client Secret을 업데이트합니다. |
| `POST` | `/settings/utilization-service-no` | Utilization Service No를 업데이트합니다. |
| `POST` | `/settings/institution-code` | Institution Code를 업데이트합니다. |
| `POST` | `/settings/seed-key` | Seed Key를 업데이트합니다. |
| `POST` | `/test-patient` | 환자 테스트를 요청합니다. |

---

#### 4. 환자 (`/api/patients`)

| HTTP 메소드 | 엔드포인트 | 설명 |
| :--- | :--- | :--- |
| `GET` | `/?clncCnfrmFlag={value}` | `clncCnfrmFlag` 값으로 환자를 조회합니다. |

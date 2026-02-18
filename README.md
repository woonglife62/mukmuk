# mukmuk (먹먹)

> "오늘 뭐 먹지?" 고민을 룰렛으로 해결하는 음식 추천 앱

[![Android](https://img.shields.io/badge/Android-24%2B-green)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-purple)](https://developer.android.com/jetpack/compose)

## 소개

**먹먹**은 매일 반복되는 "오늘 뭐 먹지?" 고민을 재미있게 해결해주는 Android 앱입니다.
룰렛을 돌려 메뉴를 정하고, 현재 위치 기반으로 주변 맛집을 바로 찾아볼 수 있습니다.

## 주요 기능

### 메뉴 룰렛
- 8개 카테고리(한식, 치킨, 일식, 중식, 양식, 분식, 카페/디저트, 동남아) **242개 메뉴**
- 카테고리 필터링으로 원하는 범위 설정
- 햅틱 피드백 + 효과음이 적용된 룰렛 애니메이션
- 결과 선택 시 카카오 API 기반 주변 맛집 자동 검색

### 즐겨찾기 룰렛
- 즐겨찾기한 맛집 중 원하는 곳을 선택하여 커스텀 룰렛 생성
- 전체 선택 / 개별 선택으로 유연한 구성
- 룰렛과 맛집 탭의 즐겨찾기 실시간 동기화

### 맛집 탐색
- 카카오 로컬 API 연동 실시간 주변 맛집 검색
- 검색 반경 설정 (500m ~ 5km)
- 별점, 리뷰 수, 거리, 카테고리 표시
- 즐겨찾기 추가/관리
- 상세보기: Leaflet.js 지도, 전화, 공유 기능

### 맛집 기록
- 방문 기록 자동 저장
- 지도 뷰에서 방문 장소 한눈에 확인

### 기타
- 점심 알림 (WorkManager 기반 푸시 알림)
- 홈 위젯 (Glance AppWidget)
- 다크/라이트 테마 지원
- 검색 반경, 햅틱, 효과음 설정

## 기술 스택

| 영역 | 기술 |
|------|------|
| **언어** | Kotlin 2.0 |
| **UI** | Jetpack Compose + Material3 |
| **아키텍처** | MVVM (ViewModel + StateFlow) |
| **DI** | Manual DI (AppContainer) |
| **로컬 DB** | Room (SQLite) |
| **설정 저장** | DataStore Preferences |
| **네트워크** | Retrofit + OkHttp + KotlinX Serialization |
| **위치** | Google Play Services Location |
| **지도** | WebView + Leaflet.js + OpenStreetMap |
| **알림** | WorkManager + NotificationCompat |
| **위젯** | Glance AppWidget |
| **빌드** | Gradle 9.2 + KSP |

## 프로젝트 구조

```
app/src/main/java/com/example/mukmuk/
├── data/
│   ├── local/          # Room DB (AppDatabase, DAO)
│   ├── model/          # 데이터 모델 (Category, Menu, Restaurant, VisitRecord)
│   ├── remote/         # 카카오 API (KakaoLocalApi, NetworkModule)
│   ├── repository/     # 저장소 (MenuRepository, SettingsRepository, HistoryRepository)
│   └── location/       # 위치 서비스
├── di/                 # 의존성 주입 (AppContainer)
├── navigation/         # 화면 라우팅 (Screen)
├── notification/       # 푸시 알림 (NotificationScheduler, LunchReminderWorker)
├── ui/
│   ├── components/     # 재사용 컴포넌트 (RouletteWheel, ResultScreen, StarRating)
│   ├── screens/        # 화면 (RouletteScreen, RestaurantsScreen, HistoryScreen, SettingsScreen)
│   ├── theme/          # 테마 (Color, Theme)
│   └── util/           # 유틸리티
├── widget/             # 홈 위젯
├── MukmukApp.kt        # 메인 네비게이션
├── MainActivity.kt     # 엔트리포인트
└── MukmukApplication.kt
```

## 화면 구성

| 탭 | 설명 |
|----|------|
| **룰렛** | 메뉴 룰렛 / 즐겨찾기 룰렛 모드 전환, 카테고리 필터, 룰렛 휠 |
| **맛집** | 카카오 API 기반 맛집 검색, 카테고리 필터, 즐겨찾기 |
| **기록** | 방문 기록 목록 + 지도 뷰 |
| **설정** | 테마, 알림, 검색 반경, 햅틱/효과음 설정 |

## 빌드 및 실행

### 요구사항
- Android Studio Ladybug 이상
- JDK 11+
- Android SDK 35 (minSdk 24)

### 환경 설정

1. 프로젝트 루트에 `.env` 파일 생성:
```
KAKAO_REST_API_KEY=your_kakao_rest_api_key
```

2. (릴리즈 빌드 시) `key.properties` 파일 생성:
```
storeFile=mukmuk-release.jks
storePassword=your_store_password
keyAlias=mukmuk
keyPassword=your_key_password
```

### 빌드
```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# AAB (Play Store 업로드용)
./gradlew bundleRelease
```

## 버전 히스토리

| 버전 | 주요 변경사항 |
|------|-------------|
| **v1.3** | 맛집 기록 시스템, 지도 뷰, 검색 범위 설정, 치킨/카페 카테고리 |
| **v1.2** | 카카오 API 연동, 수동 DI, 푸시 알림, 홈 위젯 |
| **v1.1** | ViewModel 분리, 테마 시스템, DB 확장, 즐겨찾기 |
| **v1.0** | 초기 버전 - 메뉴 룰렛, 기본 카테고리 |

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

package com.example.main

// Android Architecture Components - LiveData를 동기적으로 테스트하기 위한 Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
// Paging3 라이브러리 - 페이징 데이터 타입
import androidx.paging.PagingData
// Turbine - Flow를 테스트하기 위한 라이브러리
import app.cash.turbine.test
// 도메인 모델 - 테스트에 사용할 데이터 클래스들
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
// 도메인 UseCase - 테스트할 대상의 의존성들
import com.example.domain.usecase.media.GetMediaPagingDataUseCase
import com.example.domain.usecase.photo.DeletePhotoUseCase
import com.example.domain.usecase.photo.GetBookmarkedPhotosStateUseCase
import com.example.domain.usecase.photo.IsPhotoSavedUseCase
import com.example.domain.usecase.photo.SavePhotoUseCase
import com.example.domain.usecase.video.DeleteVideoUseCase
import com.example.domain.usecase.video.GetBookmarkedVideosStateUseCase
import com.example.domain.usecase.video.IsVideoSavedUseCase
import com.example.domain.usecase.video.SaveVideoUseCase
// MockK - Kotlin을 위한 Mocking 라이브러리
import io.mockk.coEvery  // suspend 함수를 모킹할 때 사용 (코루틴 함수)
import io.mockk.coVerify  // suspend 함수 호출 검증
import io.mockk.every  // 일반 함수를 모킹할 때 사용
import io.mockk.mockk  // Mock 객체 생성
// Kotlin Coroutines - 코루틴 테스트를 위한 라이브러리
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow  // 변경 가능한 StateFlow (테스트용)
import kotlinx.coroutines.flow.flowOf  // 고정된 값을 emit하는 Flow 생성
import kotlinx.coroutines.test.StandardTestDispatcher  // 테스트용 Dispatcher (수동으로 시간 제어 가능)
import kotlinx.coroutines.test.advanceUntilIdle  // 모든 대기 중인 코루틴 작업을 즉시 실행
import kotlinx.coroutines.test.resetMain  // Main Dispatcher를 원래대로 복구
import kotlinx.coroutines.test.runTest  // 코루틴 테스트 스코프 생성
import kotlinx.coroutines.test.setMain  // Main Dispatcher를 테스트용으로 교체
// JUnit4 - 테스트 프레임워크
import org.junit.After  // 각 테스트 이후 실행되는 메소드
import org.junit.Assert.assertEquals  // 값 동등성 검증
import org.junit.Assert.assertFalse  // false 검증
import org.junit.Assert.assertTrue  // true 검증
import org.junit.Before  // 각 테스트 이전 실행되는 메소드
import org.junit.Rule  // JUnit Rule 지정
import org.junit.Test  // 테스트 메소드 표시

// ExperimentalCoroutinesApi 사용을 명시적으로 허용
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    // InstantTaskExecutorRule: LiveData가 백그라운드 스레드가 아닌 메인 스레드에서
    // 동기적으로 동작하도록 만듦 (테스트에서 비동기 대기 없이 즉시 값 확인 가능)
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // StandardTestDispatcher: 테스트용 코루틴 디스패처
    // 코루틴의 실행 시점을 수동으로 제어할 수 있어서 테스트가 예측 가능하고 안정적임
    private val testDispatcher = StandardTestDispatcher()

    // Mock 객체들 - 실제 UseCase 대신 가짜 객체를 사용 (lateinit으로 setup()에서 초기화)
    // 이렇게 하면 실제 DB나 네트워크 없이도 ViewModel 로직만 테스트 가능
    private lateinit var getMediaPagingDataUseCase: GetMediaPagingDataUseCase
    private lateinit var savePhotoUseCase: SavePhotoUseCase
    private lateinit var saveVideoUseCase: SaveVideoUseCase
    private lateinit var deletePhotoUseCase: DeletePhotoUseCase
    private lateinit var deleteVideoUseCase: DeleteVideoUseCase
    private lateinit var isVideoSavedUseCase: IsVideoSavedUseCase
    private lateinit var isPhotoSavedUseCase: IsPhotoSavedUseCase
    private lateinit var getBookmarkedVideosStateUseCase: GetBookmarkedVideosStateUseCase
    private lateinit var getBookmarkedPhotosStateUseCase: GetBookmarkedPhotosStateUseCase

    // 테스트할 대상 - HomeViewModel
    private lateinit var viewModel: HomeViewModel

    // 테스트 데이터 - 모든 테스트에서 재사용할 수 있는 샘플 비디오 객체
    private val testVideo = Video(
        id = 100L,
        width = 1920,
        height = 1080,
        url = "test_url",
        image = "test_image",
        duration = 60,
        user = VideoUser(1L, "Test Creator", "test_url"),
        videoFiles = listOf(VideoFile(1L, "HD", "mp4", 1920, 1080, "test_link")),
        videoPictures = listOf(VideoPicture(1L, "test_picture", 0))
    )

    // 테스트 데이터 - 모든 테스트에서 재사용할 수 있는 샘플 사진 객체
    private val testPhoto = Photo(
        id = 200L,
        width = 1920,
        height = 1080,
        url = "test_url",
        photographer = "Test Photographer",
        photographerUrl = "test_url",
        avgColor = "#000000",
        src = PhotoSrc(
            original = "test_original",
            large2x = "test_large2x",
            large = "test_large",
            medium = "test_medium",
            small = "test_small",
            portrait = "test_portrait",
            landscape = "test_landscape",
            tiny = "test_tiny"
        ),
        alt = "Test photo"
    )

    // @Before: 각 테스트 메소드 실행 전에 매번 호출됨
    // 테스트 환경 초기화 (각 테스트가 독립적으로 실행되도록)
    @Before
    fun setup() {
        // Main Dispatcher를 테스트용 Dispatcher로 교체
        // ViewModel의 viewModelScope가 testDispatcher를 사용하도록 만듦
        Dispatchers.setMain(testDispatcher)

        // 모든 UseCase들을 Mock 객체로 초기화
        // mockk()는 실제 구현 없이 껍데기만 있는 가짜 객체 생성
        getMediaPagingDataUseCase = mockk()
        savePhotoUseCase = mockk()
        saveVideoUseCase = mockk()
        deletePhotoUseCase = mockk()
        deleteVideoUseCase = mockk()
        isVideoSavedUseCase = mockk()
        isPhotoSavedUseCase = mockk()
        getBookmarkedVideosStateUseCase = mockk()
        getBookmarkedPhotosStateUseCase = mockk()

        // Mock 객체들의 기본 동작 설정
        // ViewModel 생성 시 초기화 과정에서 호출되는 메소드들의 반환값 지정
        // every { ... } returns ...: "이 함수가 호출되면 이 값을 반환해라"
        every { getMediaPagingDataUseCase() } returns flowOf(PagingData.empty())  // 빈 페이징 데이터 반환
        every { getBookmarkedVideosStateUseCase() } returns flowOf(emptyMap())  // 빈 북마크 맵 반환
        every { getBookmarkedPhotosStateUseCase() } returns flowOf(emptyMap())  // 빈 북마크 맵 반환
    }

    // @After: 각 테스트 메소드 실행 후에 매번 호출됨
    // 테스트 후 정리 작업 (다음 테스트에 영향 안 가도록)
    @After
    fun tearDown() {
        // Main Dispatcher를 원래대로 복구
        // 다른 테스트나 실제 앱 코드가 영향받지 않도록
        Dispatchers.resetMain()
    }

    // 테스트 1: 저장되지 않은 비디오를 북마크하면 DB에 저장되는지 검증
    @Test
    fun `비디오 북마크 토글 - 저장되지 않은 비디오를 북마크하면 DB에 저장된다`() = runTest {
        // runTest: 코루틴 테스트 스코프 생성 (suspend 함수 호출 가능)

        // === Given (준비) ===
        // 테스트 시나리오: 저장되지 않은 비디오를 북마크하는 상황

        // coEvery: suspend 함수의 반환값을 모킹 (suspend 함수용 every)
        // isVideoSavedUseCase가 호출되면 false를 반환 (저장되지 않은 상태)
        coEvery { isVideoSavedUseCase(testVideo.id) } returns false

        // saveVideoUseCase가 호출되면 Unit(아무것도 안 함)을 반환
        coEvery { saveVideoUseCase(testVideo) } returns Unit

        // ViewModel 생성 - setup()에서 모킹한 UseCase들을 주입
        viewModel = createViewModel()

        // === When (실행) ===
        // 비디오 북마크 토글 실행
        viewModel.toggleVideoBookmark(testVideo)

        // advanceUntilIdle(): 대기 중인 모든 코루틴 작업을 즉시 완료
        // ViewModel 내부의 viewModelScope.launch가 완료될 때까지 대기
        advanceUntilIdle()

        // === Then (검증) ===
        // coVerify: suspend 함수 호출 여부를 검증
        // exactly = 1: 정확히 1번 호출되었는지 확인
        coVerify(exactly = 1) { saveVideoUseCase(testVideo) }  // 저장 메소드가 1번 호출됨

        // exactly = 0: 한 번도 호출되지 않았는지 확인
        // any<Video>(): 어떤 Video 객체든 상관없이
        coVerify(exactly = 0) { deleteVideoUseCase(any<Video>()) }  // 삭제는 호출되면 안 됨
    }

    @Test
    fun `비디오 북마크 토글 - 이미 북마크된 비디오를 다시 토글하면 DB에서 삭제된다`() = runTest {
        // Given - 이미 저장된 비디오
        coEvery { isVideoSavedUseCase(testVideo.id) } returns true
        coEvery { deleteVideoUseCase(testVideo) } returns Unit

        viewModel = createViewModel()

        // When - 북마크 토글 (삭제)
        viewModel.toggleVideoBookmark(testVideo)
        advanceUntilIdle()

        // Then - 삭제 메소드가 호출되고, 저장 메소드는 호출되지 않음
        coVerify(exactly = 1) { deleteVideoUseCase(testVideo) }
        coVerify(exactly = 0) { saveVideoUseCase(any()) }
    }

    @Test
    fun `사진 북마크 토글 - 저장되지 않은 사진을 북마크하면 DB에 저장된다`() = runTest {
        // Given - 저장되지 않은 사진
        coEvery { isPhotoSavedUseCase(testPhoto.id) } returns false
        coEvery { savePhotoUseCase(testPhoto) } returns Unit

        viewModel = createViewModel()

        // When - 북마크 토글
        viewModel.togglePhotoBookmark(testPhoto)
        advanceUntilIdle()

        // Then - 저장 메소드가 호출됨
        coVerify(exactly = 1) { savePhotoUseCase(testPhoto) }
        coVerify(exactly = 0) { deletePhotoUseCase(any<Photo>()) }
    }

    @Test
    fun `사진 북마크 토글 - 이미 북마크된 사진을 다시 토글하면 DB에서 삭제된다`() = runTest {
        // Given - 이미 저장된 사진
        coEvery { isPhotoSavedUseCase(testPhoto.id) } returns true
        coEvery { deletePhotoUseCase(testPhoto) } returns Unit

        viewModel = createViewModel()

        // When - 북마크 토글 (삭제)
        viewModel.togglePhotoBookmark(testPhoto)
        advanceUntilIdle()

        // Then - 삭제 메소드가 호출됨
        coVerify(exactly = 1) { deletePhotoUseCase(testPhoto) }
        coVerify(exactly = 0) { savePhotoUseCase(any()) }
    }

    // 테스트 5: StateFlow를 통해 북마크 상태가 실시간으로 업데이트되는지 검증
    @Test
    fun `북마크 상태 관찰 - 비디오 북마크 상태가 실시간으로 업데이트된다`() = runTest {
        // === Given (준비) ===
        // MutableStateFlow: 값을 변경할 수 있는 StateFlow (테스트에서 상태 변경 시뮬레이션용)
        val bookmarkStateFlow = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

        // UseCase가 호출되면 우리가 제어할 수 있는 MutableStateFlow 반환
        // 이렇게 하면 테스트에서 북마크 상태를 직접 변경하며 테스트 가능
        every { getBookmarkedVideosStateUseCase() } returns bookmarkStateFlow

        // ViewModel 생성 - 내부에서 bookmarkedVideos StateFlow가 생성됨
        viewModel = createViewModel()

        // === When & Then (실행 및 검증) ===
        // Turbine의 test 블록: Flow를 테스트하기 위한 특별한 스코프
        // Flow가 emit하는 값들을 순차적으로 검증 가능
        viewModel.bookmarkedVideos.test {
            // awaitItem(): Flow에서 다음 값이 emit될 때까지 대기하고 그 값을 반환
            // 첫 번째 값: 초기값 (빈 맵)
            assertEquals(emptyMap<Long, Boolean>(), awaitItem())

            // 상태 변경 시뮬레이션: DB에 북마크가 추가되었다고 가정
            // MutableStateFlow의 value를 변경하면 collect하는 곳에 자동으로 emit됨
            bookmarkStateFlow.value = mapOf(100L to true, 200L to false)

            // awaitItem()으로 변경된 값 수신
            val state1 = awaitItem()
            // 비디오 100은 북마크됨 (true)
            assertTrue("비디오 100이 북마크되어야 함", state1[100L] == true)
            // 비디오 200은 북마크 안 됨 (false)
            assertFalse("비디오 200이 북마크되지 않아야 함", state1[200L] == true)

            // 또 다른 상태 변경: 비디오 200, 300도 북마크됨
            bookmarkStateFlow.value = mapOf(100L to true, 200L to true, 300L to true)
            val state2 = awaitItem()
            assertTrue("비디오 200도 북마크되어야 함", state2[200L] == true)
            assertTrue("비디오 300도 북마크되어야 함", state2[300L] == true)

            // cancelAndIgnoreRemainingEvents(): Flow 구독 취소 및 남은 이벤트 무시
            // test 블록 종료 시 Flow가 완료되지 않았을 수 있어서 명시적으로 취소
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `북마크 상태 관찰 - 사진 북마크 상태가 실시간으로 업데이트된다`() = runTest {
        // Given - 북마크 상태를 emit하는 Flow
        val bookmarkStateFlow = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
        every { getBookmarkedPhotosStateUseCase() } returns bookmarkStateFlow

        viewModel = createViewModel()

        // When & Then - 상태 변경을 실시간으로 관찰
        viewModel.bookmarkedPhotos.test {
            // 초기값
            assertEquals(emptyMap<Long, Boolean>(), awaitItem())

            // 사진 북마크 추가
            bookmarkStateFlow.value = mapOf(200L to true)
            val state1 = awaitItem()
            assertTrue("사진 200이 북마크되어야 함", state1[200L] == true)

            // 북마크 제거
            bookmarkStateFlow.value = mapOf(200L to false)
            val state2 = awaitItem()
            assertFalse("사진 200 북마크가 해제되어야 함", state2[200L] == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `여러 비디오 연속 북마크 - 모두 정상적으로 처리된다`() = runTest {
        // Given - 3개의 비디오
        val video1 = testVideo.copy(id = 1L)
        val video2 = testVideo.copy(id = 2L)
        val video3 = testVideo.copy(id = 3L)

        coEvery { isVideoSavedUseCase(any()) } returns false
        coEvery { saveVideoUseCase(any()) } returns Unit

        viewModel = createViewModel()

        // When - 연속으로 3개 북마크
        viewModel.toggleVideoBookmark(video1)
        viewModel.toggleVideoBookmark(video2)
        viewModel.toggleVideoBookmark(video3)
        advanceUntilIdle()

        // Then - 3개 모두 저장 호출됨
        coVerify(exactly = 1) { saveVideoUseCase(video1) }
        coVerify(exactly = 1) { saveVideoUseCase(video2) }
        coVerify(exactly = 1) { saveVideoUseCase(video3) }
    }

    // 테스트 8: 같은 아이템을 빠르게 두 번 토글했을 때 순차 처리되는지 검증
    @Test
    fun `같은 비디오를 빠르게 두 번 토글 - 저장 후 삭제가 순차적으로 처리된다`() = runTest {
        // === Given (준비) ===
        // returnsMany: 호출될 때마다 다른 값을 순서대로 반환
        // 첫 번째 호출: false (저장 안 됨), 두 번째 호출: true (이미 저장됨)
        // 이렇게 하면 "저장 -> 삭제" 시나리오를 시뮬레이션 가능
        coEvery { isVideoSavedUseCase(testVideo.id) } returnsMany listOf(false, true)

        coEvery { saveVideoUseCase(testVideo) } returns Unit
        coEvery { deleteVideoUseCase(testVideo) } returns Unit

        viewModel = createViewModel()

        // === When (실행) ===
        // 같은 비디오를 연속으로 두 번 토글
        viewModel.toggleVideoBookmark(testVideo)  // 첫 번째: false -> 저장 실행
        viewModel.toggleVideoBookmark(testVideo)  // 두 번째: true -> 삭제 실행
        advanceUntilIdle()  // 모든 코루틴 작업 완료 대기

        // === Then (검증) ===
        // 저장과 삭제가 각각 정확히 1번씩 호출되었는지 확인
        // 빠른 연속 클릭에도 각 작업이 누락되지 않고 처리됨
        coVerify(exactly = 1) { saveVideoUseCase(testVideo) }
        coVerify(exactly = 1) { deleteVideoUseCase(testVideo) }
    }

    // 테스트 9: 비디오와 사진의 추가/삭제가 섞인 복잡한 시나리오 검증
    @Test
    fun `북마크 추가 삭제 혼합 시나리오 - 복잡한 시나리오도 정확히 동작한다`() = runTest {
        // === Given (준비) ===
        // 다양한 상태의 미디어 생성 (저장된 것, 안 된 것 섞어서)
        val video1 = testVideo.copy(id = 1L)  // 저장 안 됨
        val video2 = testVideo.copy(id = 2L)  // 이미 저장됨
        val photo1 = testPhoto.copy(id = 10L)  // 저장 안 됨

        // 각 미디어의 저장 상태 모킹
        coEvery { isVideoSavedUseCase(1L) } returns false  // video1: 저장 안 됨
        coEvery { isVideoSavedUseCase(2L) } returns true   // video2: 이미 저장됨
        coEvery { isPhotoSavedUseCase(10L) } returns false // photo1: 저장 안 됨

        // 각 UseCase의 동작 모킹
        coEvery { saveVideoUseCase(video1) } returns Unit    // video1 저장
        coEvery { deleteVideoUseCase(video2) } returns Unit  // video2 삭제
        coEvery { savePhotoUseCase(photo1) } returns Unit    // photo1 저장

        viewModel = createViewModel()

        // === When (실행) ===
        // 복잡한 혼합 시나리오: 비디오 추가, 비디오 삭제, 사진 추가가 연속으로 발생
        viewModel.toggleVideoBookmark(video1)   // 저장 안 됨 -> 추가
        viewModel.toggleVideoBookmark(video2)   // 이미 저장됨 -> 삭제
        viewModel.togglePhotoBookmark(photo1)   // 저장 안 됨 -> 추가
        advanceUntilIdle()  // 모든 코루틴 작업 완료 대기

        // === Then (검증) ===
        // 각 작업이 정확히 의도한 대로 실행되었는지 확인
        // 비디오/사진이 섞여도, 추가/삭제가 섞여도 모두 정확히 처리됨
        coVerify(exactly = 1) { saveVideoUseCase(video1) }    // video1은 저장됨
        coVerify(exactly = 1) { deleteVideoUseCase(video2) }  // video2는 삭제됨
        coVerify(exactly = 1) { savePhotoUseCase(photo1) }    // photo1은 저장됨
    }

    @Test
    fun `DB 조회 로직 - 북마크 여부를 정확히 체크한다`() = runTest {
        // Given
        coEvery { isVideoSavedUseCase(100L) } returns true
        coEvery { isVideoSavedUseCase(200L) } returns false
        coEvery { deleteVideoUseCase(any<Video>()) } returns Unit
        coEvery { saveVideoUseCase(any<Video>()) } returns Unit

        viewModel = createViewModel()

        // When - 두 비디오 토글
        val savedVideo = testVideo.copy(id = 100L)
        val unsavedVideo = testVideo.copy(id = 200L)

        viewModel.toggleVideoBookmark(savedVideo)    // 이미 저장됨 -> 삭제
        viewModel.toggleVideoBookmark(unsavedVideo)  // 저장 안됨 -> 추가
        advanceUntilIdle()

        // Then - 각각 올바른 작업 수행
        coVerify(exactly = 1) { isVideoSavedUseCase(100L) }
        coVerify(exactly = 1) { isVideoSavedUseCase(200L) }
        coVerify(exactly = 1) { deleteVideoUseCase(savedVideo) }
        coVerify(exactly = 1) { saveVideoUseCase(unsavedVideo) }
    }

    // 테스트 11: PagingData가 viewModelScope에 캐시되는지 검증
    // 화면 회전 등의 상황에서도 데이터를 다시 로드하지 않고 유지되는지 확인
    @Test
    fun `PagingData는 viewModelScope에 캐시되어 회전 등의 상황에서도 유지된다`() = runTest {
        // === Given (준비) ===
        // PagingData를 반환하는 UseCase 모킹
        every { getMediaPagingDataUseCase() } returns flowOf(PagingData.empty())

        // === When (실행) ===
        // ViewModel 생성 - 내부에서 cachedIn(viewModelScope)로 캐싱됨
        viewModel = createViewModel()

        // === Then (검증) ===
        // UseCase가 정확히 1번만 호출되었는지 확인
        // cachedIn()으로 인해 여러 번 collect해도 UseCase는 1번만 호출됨
        // 화면 회전 시 UI가 재구성되어도 기존 데이터를 재사용
        coVerify(exactly = 1) { getMediaPagingDataUseCase() }
    }

    /**
     * ViewModel 생성 헬퍼 함수
     *
     * 모든 테스트에서 동일한 방식으로 ViewModel을 생성하기 위한 팩토리 함수
     * setup()에서 모킹한 UseCase들을 주입하여 ViewModel 인스턴스 생성
     *
     * 이렇게 하면:
     * 1. 테스트 코드 중복 제거
     * 2. ViewModel 생성자 변경 시 한 곳만 수정하면 됨
     * 3. 각 테스트는 필요한 Mock 동작만 설정하고 이 함수로 생성
     */
    private fun createViewModel() = HomeViewModel(
        getMediaPagingDataUseCase = getMediaPagingDataUseCase,
        savePhotoUseCase = savePhotoUseCase,
        saveVideoUseCase = saveVideoUseCase,
        deletePhotoUseCase = deletePhotoUseCase,
        deleteVideoUseCase = deleteVideoUseCase,
        isVideoSavedUseCase = isVideoSavedUseCase,
        isPhotoSavedUseCase = isPhotoSavedUseCase,
        getBookmarkedVideosStateUseCase = getBookmarkedVideosStateUseCase,
        getBookmarkedPhotosStateUseCase = getBookmarkedPhotosStateUseCase
    )
}

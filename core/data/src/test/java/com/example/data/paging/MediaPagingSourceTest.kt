package com.example.data.paging

import androidx.paging.PagingSource
import com.example.data.datasource.local.MediaLocalDataSource
import com.example.data.datasource.remote.MediaRemoteDataSource
import com.example.domain.model.MediaItem
import com.example.domain.model.Photo
import com.example.domain.model.PhotoSrc
import com.example.domain.model.Video
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
import com.example.network.model.CuratedPhotosResponse
import com.example.network.model.PhotoDto
import com.example.network.model.PhotoSrcDto
import com.example.network.model.PopularVideoListResponse
import com.example.network.model.VideoDto
import com.example.network.model.VideoFileDto
import com.example.network.model.VideoPictureDto
import com.example.network.model.VideoUserDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * MediaPagingSource 테스트
 *
 * 실제 API 호출이 정상적으로 동작하는지 검증
 * Mock을 사용하여 네트워크 요청 없이 PagingSource 로직만 테스트
 */
class MediaPagingSourceTest {

    // Mock 객체들
    private lateinit var remoteDataSource: MediaRemoteDataSource
    private lateinit var localDataSource: MediaLocalDataSource
    private lateinit var pagingSource: MediaPagingSource

    private val testApiKey = "test_api_key_12345"

    // 테스트용 DTO (네트워크에서 받는 형식)
    private val testVideoDto = VideoDto(
        id = 100L,
        width = 1920,
        height = 1080,
        url = "https://pexels.com/video/100",
        image = "https://pexels.com/image/100.jpg",
        duration = 60,
        user = VideoUserDto(1L, "Test Creator", "https://pexels.com/user/1"),
        videoFiles = listOf(VideoFileDto(1L, "HD", "mp4", 1920, 1080, "https://pexels.com/video.mp4")),
        videoPictures = listOf(VideoPictureDto(1L, "https://pexels.com/thumb.jpg", 0))
    )

    private val testPhotoDto = PhotoDto(
        id = 200L,
        width = 1920,
        height = 1080,
        url = "https://pexels.com/photo/200",
        photographer = "Test Photographer",
        photographerUrl = "https://pexels.com/photographer",
        avgColor = "#000000",
        src = PhotoSrcDto(
            original = "https://pexels.com/photo-original.jpg",
            large2x = "https://pexels.com/photo-large2x.jpg",
            large = "https://pexels.com/photo-large.jpg",
            medium = "https://pexels.com/photo-medium.jpg",
            small = "https://pexels.com/photo-small.jpg",
            portrait = "https://pexels.com/photo-portrait.jpg",
            landscape = "https://pexels.com/photo-landscape.jpg",
            tiny = "https://pexels.com/photo-tiny.jpg"
        ),
        alt = "Test photo description"
    )

    @Before
    fun setup() {
        // Mock 객체 생성
        remoteDataSource = mockk()
        localDataSource = mockk()

        // PagingSource 생성
        pagingSource = MediaPagingSource(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            apiKey = testApiKey
        )
    }

    // ========== 정상 동작 테스트 ==========

    @Test
    fun `첫 페이지 로드 시 비디오와 사진을 섞어서 반환한다`() = runTest {
        // === Given (준비) ===
        // API 응답 Mock 설정 (2개의 비디오, 2개의 사진)
        val videoResponse = PopularVideoListResponse(
            page = 1,
            perPage = 10,
            videos = listOf(
                testVideoDto.copy(id = 1L),
                testVideoDto.copy(id = 2L)
            ),
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = "https://pexels.com/videos?page=2"
        )

        val photoResponse = CuratedPhotosResponse(
            page = 1,
            perPage = 10,
            photos = listOf(
                testPhotoDto.copy(id = 101L),
                testPhotoDto.copy(id = 102L)
            ),
            totalResults = 100,
            nextPage = "https://pexels.com/photos?page=2"
        )

        // RemoteDataSource의 API 호출 결과를 Mock으로 지정
        coEvery {
            remoteDataSource.getPopularVideos(
                apiKey = testApiKey,
                page = 1,
                perPage = any()
            )
        } returns videoResponse

        coEvery {
            remoteDataSource.getCuratedPhotos(
                apiKey = testApiKey,
                page = 1,
                perPage = any()
            )
        } returns photoResponse

        // === When (실행) ===
        // PagingSource.load() 호출 - 첫 페이지 요청
        val loadParams = PagingSource.LoadParams.Refresh(
            key = null,  // null이면 첫 페이지
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then (검증) ===
        // 1. 결과가 성공(Page)인지 확인
        assertTrue("로드 결과가 Page여야 함", result is PagingSource.LoadResult.Page)

        val pageResult = result as PagingSource.LoadResult.Page

        // 2. 데이터 개수 확인 (비디오 2개 + 사진 2개 = 4개)
        assertEquals("비디오와 사진을 합쳐 4개여야 함", 4, pageResult.data.size)

        // 3. 비디오와 사진이 번갈아 나오는지 확인 (섞여있는지)
        assertTrue("첫 번째는 VideoItem이어야 함", pageResult.data[0] is MediaItem.VideoItem)
        assertTrue("두 번째는 PhotoItem이어야 함", pageResult.data[1] is MediaItem.PhotoItem)
        assertTrue("세 번째는 VideoItem이어야 함", pageResult.data[2] is MediaItem.VideoItem)
        assertTrue("네 번째는 PhotoItem이어야 함", pageResult.data[3] is MediaItem.PhotoItem)

        // 4. 페이징 키 확인
        assertEquals("이전 페이지는 null이어야 함", null, pageResult.prevKey)
        assertEquals("다음 페이지는 2여야 함", 2, pageResult.nextKey)

        // 5. API가 정확히 1번씩 호출되었는지 검증
        coVerify(exactly = 1) {
            remoteDataSource.getPopularVideos(testApiKey, 1, any())
        }
        coVerify(exactly = 1) {
            remoteDataSource.getCuratedPhotos(testApiKey, 1, any())
        }
    }

    @Test
    fun `두 번째 페이지 로드 시 page 파라미터가 2로 전달된다`() = runTest {
        // === Given ===
        val videoResponse = PopularVideoListResponse(
            page = 2,
            perPage = 10,
            videos = listOf(testVideoDto.copy(id = 3L)),
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = "https://pexels.com/videos?page=3"
        )

        val photoResponse = CuratedPhotosResponse(
            page = 2,
            perPage = 10,
            photos = listOf(testPhotoDto.copy(id = 103L)),
            totalResults = 100,
            nextPage = "https://pexels.com/photos?page=3"
        )

        coEvery {
            remoteDataSource.getPopularVideos(testApiKey, 2, any())
        } returns videoResponse

        coEvery {
            remoteDataSource.getCuratedPhotos(testApiKey, 2, any())
        } returns photoResponse

        // === When ===
        // 두 번째 페이지 요청 (key = 2)
        val loadParams = PagingSource.LoadParams.Refresh(
            key = 2,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then ===
        assertTrue("로드 결과가 Page여야 함", result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page

        // 페이징 키 확인
        assertEquals("이전 페이지는 1이어야 함", 1, pageResult.prevKey)
        assertEquals("다음 페이지는 3이어야 함", 3, pageResult.nextKey)

        // API가 page=2로 호출되었는지 검증
        coVerify(exactly = 1) {
            remoteDataSource.getPopularVideos(testApiKey, 2, any())
        }
        coVerify(exactly = 1) {
            remoteDataSource.getCuratedPhotos(testApiKey, 2, any())
        }
    }

    @Test
    fun `빈 응답이 오면 nextKey가 null이다`() = runTest {
        // === Given ===
        // 빈 응답 (더 이상 데이터가 없는 경우)
        val emptyVideoResponse = PopularVideoListResponse(
            page = 5,
            perPage = 10,
            videos = emptyList(),  // 빈 리스트
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = null
        )

        val emptyPhotoResponse = CuratedPhotosResponse(
            page = 5,
            perPage = 10,
            photos = emptyList(),  // 빈 리스트
            totalResults = 100,
            nextPage = null
        )

        coEvery {
            remoteDataSource.getPopularVideos(testApiKey, 5, any())
        } returns emptyVideoResponse

        coEvery {
            remoteDataSource.getCuratedPhotos(testApiKey, 5, any())
        } returns emptyPhotoResponse

        // === When ===
        val loadParams = PagingSource.LoadParams.Refresh(
            key = 5,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then ===
        assertTrue("로드 결과가 Page여야 함", result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page

        assertEquals("데이터가 없어야 함", 0, pageResult.data.size)
        assertEquals("nextKey가 null이어야 함 (더 이상 페이지 없음)", null, pageResult.nextKey)
    }

    // ========== 에러 처리 테스트 ==========

    @Test
    fun `API 호출 실패 시 LoadResult_Error를 반환한다`() = runTest {
        // === Given ===
        // API 호출 시 예외 발생 시뮬레이션
        val expectedException = Exception("Network error: API request failed")

        coEvery {
            remoteDataSource.getPopularVideos(any(), any(), any())
        } throws expectedException

        // === When ===
        val loadParams = PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then ===
        // 1. 결과가 Error인지 확인
        assertTrue("로드 결과가 Error여야 함", result is PagingSource.LoadResult.Error)

        // 2. 예외 메시지 확인
        val errorResult = result as PagingSource.LoadResult.Error
        assertEquals("예외 메시지가 일치해야 함", expectedException, errorResult.throwable)
    }

    @Test
    fun `비디오는 성공하지만 사진 API 실패 시 Error를 반환한다`() = runTest {
        // === Given ===
        val videoResponse = PopularVideoListResponse(
            page = 1,
            perPage = 10,
            videos = listOf(testVideoDto),
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = "https://pexels.com/videos?page=2"
        )

        // 비디오는 성공
        coEvery {
            remoteDataSource.getPopularVideos(any(), any(), any())
        } returns videoResponse

        // 사진은 실패
        coEvery {
            remoteDataSource.getCuratedPhotos(any(), any(), any())
        } throws Exception("Photo API failed")

        // === When ===
        val loadParams = PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then ===
        assertTrue("하나라도 실패하면 Error를 반환해야 함", result is PagingSource.LoadResult.Error)
    }

    // ========== 데이터 변환 테스트 ==========

    @Test
    fun `DTO가 Domain 모델로 정확히 변환된다`() = runTest {
        // === Given ===
        val videoResponse = PopularVideoListResponse(
            page = 1,
            perPage = 10,
            videos = listOf(testVideoDto),
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = null
        )

        val photoResponse = CuratedPhotosResponse(
            page = 1,
            perPage = 10,
            photos = listOf(testPhotoDto),
            totalResults = 100,
            nextPage = null
        )

        coEvery {
            remoteDataSource.getPopularVideos(any(), any(), any())
        } returns videoResponse

        coEvery {
            remoteDataSource.getCuratedPhotos(any(), any(), any())
        } returns photoResponse

        // === When ===
        val loadParams = PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(loadParams)

        // === Then ===
        assertTrue(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page

        // VideoItem 변환 검증
        val videoItem = pageResult.data[0] as MediaItem.VideoItem
        assertEquals("비디오 ID가 일치해야 함", 100L, videoItem.video.id)
        assertEquals("비디오 URL이 일치해야 함", "https://pexels.com/video/100", videoItem.video.url)

        // PhotoItem 변환 검증
        val photoItem = pageResult.data[1] as MediaItem.PhotoItem
        assertEquals("사진 ID가 일치해야 함", 200L, photoItem.photo.id)
        assertEquals("사진 URL이 일치해야 함", "https://pexels.com/photo/200", photoItem.photo.url)
    }

    // ========== 페이지 크기 테스트 ==========

    @Test
    fun `loadSize가 20이면 비디오 10개_사진 10개를 요청한다`() = runTest {
        // === Given ===
        val videoResponse = PopularVideoListResponse(
            page = 1,
            perPage = 10,
            videos = emptyList(),
            totalResults = 100,
            url = "https://pexels.com/videos",
            nextPage = null
        )

        val photoResponse = CuratedPhotosResponse(
            page = 1,
            perPage = 10,
            photos = emptyList(),
            totalResults = 100,
            nextPage = null
        )

        coEvery {
            remoteDataSource.getPopularVideos(any(), any(), any())
        } returns videoResponse

        coEvery {
            remoteDataSource.getCuratedPhotos(any(), any(), any())
        } returns photoResponse

        // === When ===
        val loadParams = PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 20,  // 전체 20개
            placeholdersEnabled = false
        )

        pagingSource.load(loadParams)

        // === Then ===
        // perPage가 10(20/2)으로 호출되었는지 검증
        coVerify {
            remoteDataSource.getPopularVideos(testApiKey, 1, 10)
        }
        coVerify {
            remoteDataSource.getCuratedPhotos(testApiKey, 1, 10)
        }
    }
}

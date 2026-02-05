package com.example.data.dao

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.datasource.local.db.GrensilVideoListDatabase
import com.example.data.datasource.local.db.dao.VideoDao
import com.example.data.datasource.local.db.entity.VideoEntity
import com.example.domain.model.VideoFile
import com.example.domain.model.VideoPicture
import com.example.domain.model.VideoUser
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * VideoDao 실제 DB 테스트
 *
 * AndroidJUnit4: Android 컨텍스트를 사용하는 Instrumented Test
 * 실제 Room Database를 생성하여 DB 작업이 정상적으로 동작하는지 검증
 */
@RunWith(AndroidJUnit4::class)
class VideoDaoTest {

    // InstantTaskExecutorRule: LiveData와 Flow를 동기적으로 테스트
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // 실제 Room Database 인스턴스
    private lateinit var database: GrensilVideoListDatabase
    // 테스트할 DAO
    private lateinit var videoDao: VideoDao

    // 테스트용 샘플 비디오 데이터
    private val testVideo1 = VideoEntity(
        id = 1L,
        width = 1920,
        height = 1080,
        url = "https://test.com/video1",
        image = "https://test.com/image1.jpg",
        duration = 60,
        user = VideoUser(1L, "Creator 1", "https://test.com/user1"),
        videoFiles = listOf(VideoFile(1L, "HD", "mp4", 1920, 1080, "https://test.com/video1.mp4")),
        videoPictures = listOf(VideoPicture(1L, "https://test.com/thumb1.jpg", 0))
    )

    private val testVideo2 = VideoEntity(
        id = 2L,
        width = 1280,
        height = 720,
        url = "https://test.com/video2",
        image = "https://test.com/image2.jpg",
        duration = 120,
        user = VideoUser(2L, "Creator 2", "https://test.com/user2"),
        videoFiles = listOf(VideoFile(2L, "SD", "mp4", 1280, 720, "https://test.com/video2.mp4")),
        videoPictures = listOf(VideoPicture(2L, "https://test.com/thumb2.jpg", 0))
    )

    @Before
    fun setup() {
        // ApplicationProvider: 테스트용 Context 제공
        val context = ApplicationProvider.getApplicationContext<Context>()

        // inMemoryDatabaseBuilder: 메모리에만 존재하는 DB 생성
        // - 실제 파일로 저장되지 않음
        // - 테스트 간 격리 (각 테스트마다 새 DB)
        // - 빠른 실행 속도
        database = Room.inMemoryDatabaseBuilder(
            context,
            GrensilVideoListDatabase::class.java
        )
            .allowMainThreadQueries()  // 테스트에서는 메인 스레드에서 쿼리 허용
            .build()

        videoDao = database.videoDao()
    }

    @After
    fun tearDown() {
        // 테스트 후 DB 닫기 (메모리 해제)
        database.close()
    }

    // ========== 기본 CRUD 테스트 ==========

    @Test
    fun 비디오를_DB에_저장하고_ID로_조회하면_같은_데이터를_반환한다() = runTest {
        // === Given (준비) ===
        val video = testVideo1

        // === When (실행) ===
        // 1. DB에 비디오 저장
        videoDao.insertVideo(video)

        // 2. 저장한 비디오를 ID로 조회
        // first(): Flow의 첫 번째 값을 가져옴 (Flow를 한 번만 collect)
        val retrievedVideo = videoDao.getVideoById(video.id).first()

        // === Then (검증) ===
        // 조회한 비디오가 null이 아니고, 저장한 데이터와 동일한지 확인
        assertNotNull("비디오가 DB에 저장되어야 함", retrievedVideo)
        assertEquals("저장한 비디오와 조회한 비디오가 같아야 함", video, retrievedVideo)
    }

    @Test
    fun 여러_비디오를_한_번에_저장하고_전체_조회하면_모두_반환된다() = runTest {
        // === Given ===
        val videos = listOf(testVideo1, testVideo2)

        // === When ===
        // insertVideos: List로 한 번에 여러 개 저장
        videoDao.insertVideos(videos)

        // getAllVideos: 전체 비디오 조회
        val allVideos = videoDao.getAllVideos().first()

        // === Then ===
        assertEquals("저장한 비디오 개수와 조회한 개수가 같아야 함", 2, allVideos.size)
        assertEquals("첫 번째 비디오가 일치해야 함", testVideo2, allVideos[0])  // ORDER BY id DESC
        assertEquals("두 번째 비디오가 일치해야 함", testVideo1, allVideos[1])
    }

    @Test
    fun 같은_ID의_비디오를_저장하면_기존_데이터가_교체된다() = runTest {
        // === Given ===
        // OnConflictStrategy.REPLACE 동작 테스트
        val originalVideo = testVideo1
        val updatedVideo = testVideo1.copy(
            duration = 999,  // duration 변경
            url = "https://updated.com/video1"
        )

        // === When ===
        videoDao.insertVideo(originalVideo)  // 첫 번째 저장
        videoDao.insertVideo(updatedVideo)   // 같은 ID로 다시 저장 (교체)

        val retrievedVideo = videoDao.getVideoById(originalVideo.id).first()

        // === Then ===
        assertNotNull(retrievedVideo)
        assertEquals("업데이트된 duration이어야 함", 999, retrievedVideo?.duration)
        assertEquals("업데이트된 URL이어야 함", "https://updated.com/video1", retrievedVideo?.url)
    }

    @Test
    fun 비디오를_업데이트하면_변경사항이_DB에_반영된다() = runTest {
        // === Given ===
        videoDao.insertVideo(testVideo1)

        // === When ===
        // 비디오 수정 후 업데이트
        val updatedVideo = testVideo1.copy(duration = 180)
        videoDao.updateVideo(updatedVideo)

        val retrievedVideo = videoDao.getVideoById(testVideo1.id).first()

        // === Then ===
        assertEquals("변경된 duration이 반영되어야 함", 180, retrievedVideo?.duration)
    }

    @Test
    fun 비디오를_삭제하면_DB에서_조회되지_않는다() = runTest {
        // === Given ===
        videoDao.insertVideo(testVideo1)

        // === When ===
        // 비디오 삭제
        videoDao.deleteVideo(testVideo1)

        val retrievedVideo = videoDao.getVideoById(testVideo1.id).first()

        // === Then ===
        // 삭제 후 조회하면 null 반환
        assertNull("삭제된 비디오는 조회되지 않아야 함", retrievedVideo)
    }

    @Test
    fun 비디오를_ID로_삭제하면_DB에서_제거된다() = runTest {
        // === Given ===
        videoDao.insertVideo(testVideo1)

        // === When ===
        // ID만으로 삭제 (Entity 객체 없이)
        videoDao.deleteVideoById(testVideo1.id)

        val retrievedVideo = videoDao.getVideoById(testVideo1.id).first()

        // === Then ===
        assertNull("ID로 삭제한 비디오는 조회되지 않아야 함", retrievedVideo)
    }

    @Test
    fun 모든_비디오를_삭제하면_DB가_비워진다() = runTest {
        // === Given ===
        videoDao.insertVideos(listOf(testVideo1, testVideo2))

        // === When ===
        videoDao.deleteAllVideos()

        val allVideos = videoDao.getAllVideos().first()

        // === Then ===
        assertEquals("모든 비디오가 삭제되어 빈 리스트여야 함", 0, allVideos.size)
    }

    // ========== 쿼리 동작 테스트 ==========

    @Test
    fun 비디오_개수를_정확히_카운트한다() = runTest {
        // === Given ===
        videoDao.insertVideos(listOf(testVideo1, testVideo2))

        // === When ===
        val count = videoDao.getVideoCount()

        // === Then ===
        assertEquals("비디오 개수가 2개여야 함", 2, count)
    }

    @Test
    fun 존재하지_않는_ID로_조회하면_null을_반환한다() = runTest {
        // === When ===
        val retrievedVideo = videoDao.getVideoById(999L).first()

        // === Then ===
        assertNull("존재하지 않는 ID는 null을 반환해야 함", retrievedVideo)
    }

    @Test
    fun suspend_함수로_단건_조회가_정상_동작한다() = runTest {
        // === Given ===
        videoDao.insertVideo(testVideo1)

        // === When ===
        // Flow가 아닌 suspend 함수로 직접 조회
        val retrievedVideo = videoDao.getVideoByIdOnce(testVideo1.id)

        // === Then ===
        assertNotNull("suspend 함수로 조회한 비디오가 있어야 함", retrievedVideo)
        assertEquals("조회한 비디오가 일치해야 함", testVideo1, retrievedVideo)
    }

    // ========== Flow 동작 테스트 ==========

    @Test
    fun Flow는_DB_변경을_실시간으로_반영한다() = runTest {
        // === Given ===
        videoDao.insertVideo(testVideo1)

        // === When & Then ===
        // 첫 번째 조회
        val firstResult = videoDao.getVideoById(testVideo1.id).first()
        assertEquals("초기 duration이 60이어야 함", 60, firstResult?.duration)

        // 데이터 변경
        val updatedVideo = testVideo1.copy(duration = 200)
        videoDao.updateVideo(updatedVideo)

        // 두 번째 조회 - Flow는 변경된 값을 자동으로 emit
        val secondResult = videoDao.getVideoById(testVideo1.id).first()
        assertEquals("업데이트된 duration이 200이어야 함", 200, secondResult?.duration)
    }

    // ========== 복잡한 TypeConverter 테스트 ==========

    @Test
    fun 복잡한_객체_타입이_정상적으로_저장되고_조회된다() = runTest {
        // VideoEntity는 VideoUser, List<VideoFile>, List<VideoPicture> 등
        // 복잡한 타입들을 포함하고 있음
        // TypeConverter가 제대로 동작하는지 검증

        // === Given ===
        val video = testVideo1.copy(
            videoFiles = listOf(
                VideoFile(1L, "HD", "mp4", 1920, 1080, "link1"),
                VideoFile(2L, "SD", "mp4", 1280, 720, "link2")
            ),
            videoPictures = listOf(
                VideoPicture(1L, "pic1", 0),
                VideoPicture(2L, "pic2", 1)
            )
        )

        // === When ===
        videoDao.insertVideo(video)
        val retrievedVideo = videoDao.getVideoById(video.id).first()

        // === Then ===
        assertNotNull(retrievedVideo)
        assertEquals("VideoFiles 리스트가 정확히 저장/조회되어야 함", 2, retrievedVideo?.videoFiles?.size)
        assertEquals("VideoPictures 리스트가 정확히 저장/조회되어야 함", 2, retrievedVideo?.videoPictures?.size)
        assertEquals("VideoUser 객체가 정확히 저장/조회되어야 함", video.user, retrievedVideo?.user)
    }
}

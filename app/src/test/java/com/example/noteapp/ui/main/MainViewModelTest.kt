package com.example.noteapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.noteapp.data.Repository
import com.example.noteapp.data.model.Note
import com.example.noteapp.data.model.NoteResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    private val mockRepository: Repository = mockk()
    private val mockNotes = MutableLiveData<NoteResult>()
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        every { mockRepository.getNotes() } returns mockNotes
        mainViewModel = MainViewModel(mockRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `should call getNotes once`() {
        verify(exactly = 1) { mockRepository.getNotes() }
    }

    @Test
    fun `should return error`() {
        var result: Throwable? = null
        val testData = Throwable("error")
        mainViewModel.getViewState().observeForever { result = it?.error }
        mockNotes.value = NoteResult.Error(testData)
        assertEquals(result, testData)
    }

    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"))
        mainViewModel.getViewState().observeForever { result = it?.data }
        mockNotes.value = NoteResult.Success(testData)
        assertEquals(testData, result)
    }

    @Test
    fun `should remove observer`() {
        mainViewModel.onCleared()
        assertFalse(mockNotes.hasObservers())
    }
}
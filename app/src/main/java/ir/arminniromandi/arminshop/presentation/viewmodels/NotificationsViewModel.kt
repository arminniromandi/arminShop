package ir.arminniromandi.arminshop.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.arminniromandi.arminshop.data.local.entity.NotificationEntity
import ir.arminniromandi.arminshop.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter {
    ALL,
    UNREAD,
    READ
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(NotificationFilter.ALL)
    val selectedFilter: StateFlow<NotificationFilter> = _selectedFilter.asStateFlow()

    private val allNotifications = notificationRepository.getAllNotifications()

    val filteredNotifications: StateFlow<List<NotificationEntity>> = combine(
        allNotifications,
        _selectedFilter
    ) { notifications, filter ->
        when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.READ -> notifications.filter { it.isRead }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: NotificationFilter) {
        if (_selectedFilter.value == filter) {
            _selectedFilter.value = NotificationFilter.ALL
        } else {
            _selectedFilter.value = filter
        }
    }

    fun markAsRead(id: Long) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    fun toggleReadStatus(id: Long, isRead: Boolean) {
        viewModelScope.launch {
            notificationRepository.updateReadStatus(id, isRead)
        }
    }
}

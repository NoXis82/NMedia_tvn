package ru.netology.nmedia.di.module.factory

import androidx.work.DelegatingWorkerFactory
import ru.netology.nmedia.repository.IPostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DependencyWorkerFactory @Inject constructor(
    repository: IPostRepository
) : DelegatingWorkerFactory() {
    init {
        addFactory(RefreshPostsWorkerFactory(repository))
        addFactory(RemovePostWorkerFactory(repository))
        addFactory(SavePostWorkerFactory(repository))
    }
}
/*
 * Copyright (c) 2026 MeecoLabs
 * SPDX-License-Identifier: GPL-3.0-only
 */

package eu.meecolabs.heshunt

import android.app.Application
import eu.meecolabs.heshunt.di.MainApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin<MainApp> {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}

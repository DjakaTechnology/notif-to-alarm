//
//  ContentView.swift
//  NotifyToAlarm
//
//  Created by Djaka Pradana Jaya Priambudi on 28/03/24.
//

import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                        .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

#Preview {
    ContentView()
}

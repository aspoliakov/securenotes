//
//  ContentView.swift
//  Securenotes
//
//  Created by Anton Poliakov on 01.12.2023.
//

import UIKit
import SwiftUI
import app

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all) // Compose has own keyboard handler
            .onOpenURL { url in
                _ = IosGoogleSignInLauncher.handle(url: url)
            }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.mainViewController(
            appConfigProvider: IosAppConfigProvider(),
            googleSignInLauncher: IosGoogleSignInLauncher()
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

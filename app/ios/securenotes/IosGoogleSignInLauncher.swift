//
//  IosGoogleSignInLauncher.swift
//  Securenotes
//

import GoogleSignIn
import UIKit
import app

class IosGoogleSignInLauncher: NSObject, GoogleSignInLauncher {

    func signIn(
        serverClientId: String,
        onIdTokenReceived: @escaping (String) -> Void,
        onCancelled: @escaping () -> Void,
        onError: @escaping () -> Void
    ) {
        guard let clientId = Self.readClientId(), let presenter = Self.topViewController() else {
            onError()
            return
        }
        let signIn = GIDSignIn.sharedInstance
        signIn.configuration = GIDConfiguration(clientID: clientId, serverClientID: serverClientId)
        // Same as Android: always show the account picker instead of silently reusing the previous account
        signIn.signOut()
        signIn.signIn(withPresenting: presenter) { result, error in
            if let error = error {
                if (error as NSError).code == GIDSignInError.canceled.rawValue {
                    onCancelled()
                } else {
                    onError()
                }
                return
            }
            // With serverClientID set, the ID token's audience is the web client id the backend verifies
            if let idToken = result?.user.idToken?.tokenString {
                onIdTokenReceived(idToken)
            } else {
                onError()
            }
        }
    }

    static func handle(url: URL) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }

    private static func readClientId() -> String? {
        guard let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
              let plist = NSDictionary(contentsOfFile: path) else {
            return nil
        }
        return plist["CLIENT_ID"] as? String
    }

    private static func topViewController() -> UIViewController? {
        let keyWindow = UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }
        var top = keyWindow?.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}

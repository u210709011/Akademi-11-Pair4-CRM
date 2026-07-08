export type Lang = 'en' | 'tr';

export const TRANSLATIONS: Record<Lang, Record<string, string>> = {
  en: {
    'auth.heroPrefix': 'The complete',
    'auth.heroHighlight': 'customer',
    'auth.heroSuffix': 'workspace for your frontline.',
    'auth.heroSubtitle': 'Customer onboarding, search and profile management.',
    'auth.footerProduct': 'CRM Lite v1.0',
    'auth.footerSecure': 'Secure enterprise sign-in',

    'login.badge': 'Welcome back',
    'login.title': 'Sign in to CRM Lite',
    'login.subtitle': 'Use your Etiya corporate account to continue.',
    'login.usernameLabel': 'Username',
    'login.usernamePlaceholder': 'Enter your username',
    'login.passwordLabel': 'Password',
    'login.passwordPlaceholder': 'Enter your password',
    'login.showPassword': 'Show password',
    'login.hidePassword': 'Hide password',
    'login.submit': 'Sign in',
    'login.demoLogin': 'Demo login'
  },
  tr: {
    'auth.heroPrefix': 'Saha ekibiniz için eksiksiz',
    'auth.heroHighlight': 'müşteri',
    'auth.heroSuffix': 'çalışma alanı.',
    'auth.heroSubtitle': 'Müşteri kaydı, arama ve profil yönetimi.',
    'auth.footerProduct': 'CRM Lite v1.0',
    'auth.footerSecure': 'Güvenli kurumsal giriş',

    'login.badge': 'Tekrar hoş geldiniz',
    'login.title': "CRM Lite'a giriş yapın",
    'login.subtitle': 'Devam etmek için Etiya kurumsal hesabınızı kullanın.',
    'login.usernameLabel': 'Kullanıcı adı',
    'login.usernamePlaceholder': 'Kullanıcı adınızı girin',
    'login.passwordLabel': 'Şifre',
    'login.passwordPlaceholder': 'Şifrenizi girin',
    'login.showPassword': 'Şifreyi göster',
    'login.hidePassword': 'Şifreyi gizle',
    'login.submit': 'Giriş yap',
    'login.demoLogin': 'Demo giriş'
  }
};

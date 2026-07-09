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
    'login.demoLogin': 'Demo login',
    'login.wrongCredentials': 'Wrong user name or password. Please try again.',
    'login.accountLocked': 'Your account has been locked. Please try again after 15 minutes.',

    'sidebar.b2c' : 'B2C',
    'sidebar.b2b' : 'B2B',
    'sidebar.approvals' : 'Approvals',
    'sidebar.logout' : 'Log out',

    'navbar.myProfile' : 'My Profile',
    'navbar.logout' : 'Log out',

    'search.searchCustomer' : 'Search Customer',
    'search.b2c' : 'B2C',
    'search.searchFilters' : 'Search Filters',
    'search.results' : 'Search Results',
    'search.natId' : 'National ID Number',
    'search.custId' : 'Customer ID',
    'search.accNum' : 'Account Number',
    'search.gsm' : 'GSM Number',
    'search.firstName' : 'First Name',
    'search.lastName' : 'Last Name',
    'search.ordNum' : 'Order Number',
    'search.clearBtn' : 'Clear',
    'search.searchBtn' : 'Search',
    'search.title' : 'Search for a customer',
    'search.subTitle' : 'Enter one or more criteria on the left and click Search to find an existing customer.'



  },
  tr: {
    'auth.heroPrefix': 'Ekibiniz için eksiksiz',
    'auth.heroHighlight': 'müşteri',
    'auth.heroSuffix': 'çalışma alanı.',
    'auth.heroSubtitle': 'Müşteri kaydı, arama ve profil yönetimi.',
    'auth.footerProduct': 'CRM Lite v1.0',
    'auth.footerSecure': 'Güvenli kurumsal giriş',

    'login.badge': 'Tekrar hoş geldiniz',
    'login.title': "CRM Lite'a Giriş Yapın",
    'login.subtitle': 'Devam etmek için Etiya kurumsal hesabınızı kullanın.',
    'login.usernameLabel': 'Kullanıcı Adı',
    'login.usernamePlaceholder': 'Kullanıcı adınızı girin',
    'login.passwordLabel': 'Parola',
    'login.passwordPlaceholder': 'Parolanızı girin',
    'login.showPassword': 'Parolayı göster',
    'login.hidePassword': 'Parolayı gizle',
    'login.submit': 'Giriş yap',
    'login.demoLogin': 'Demo giriş',
    'login.wrongCredentials': 'Kullanıcı adı veya parola hatalı. Lütfen tekrar deneyin.',
    'login.accountLocked': 'Hesabınız kilitlendi. Lütfen 15 dakika sonra tekrar deneyin.',

    'sidebar.b2c' : 'B2C',
    'sidebar.b2b' : 'B2B',
    'sidebar.approvals' : 'Onaylar',
    'sidebar.logout' : 'Çıkış',

    'navbar.myProfile' : 'Profilim',
    'navbar.logout' : 'Çkış',

    'search.searchCustomer' : 'Müşteri Ara',
    'search.b2c' : 'B2C',
    'search.searchFilters' : 'Arama Filtreleri',
    'search.results' : 'Arama Sonuçları',
    'search.natId' : 'TC Kimlik Numarası',
    'search.custId' : 'Müşteri Numarası',
    'search.accNum' : 'Hesap Numarası',
    'search.gsm' : 'GSM NUmarası',
    'search.firstName' : 'Ad',
    'search.lastName' : 'Soyad',
    'search.ordNum' : 'Sipariş Numarası',
    'search.clearBtn' : 'Temizle',
    'search.searchBtn' : 'Ara',
    'search.title' : 'Bir müşteri arayın',
    'search.subTitle' : "Soldan bir veya daha fazla kriter girin ve mevcut bir müşteriyi bulmak için Ara'ya tıklayın."



  }
};

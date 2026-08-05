/* eslint-disable */
// Postman koleksiyonundan Xray Test Case Importer icin CSV uretir.
const fs = require('fs');
const path = require('path');

const ROOT = path.join(__dirname, '..', '..');
const COLLECTION = path.join(ROOT, 'postman/CRM-Lite-FR001-FR011.postman_collection.json');
const ALLURE = path.join(ROOT, 'postman/newman/allure-results');
const OUT_DIR = path.join(ROOT, 'jira');

const col = JSON.parse(fs.readFileSync(COLLECTION, 'utf8'));

// ---------------------------------------------------------------- son kosum
const runStatus = {};
try {
  fs.readdirSync(ALLURE).filter(f => f.endsWith('-result.json')).forEach(f => {
    const j = JSON.parse(fs.readFileSync(path.join(ALLURE, f), 'utf8'));
    const m = String(j.name || '').match(/^(TC-\d{3}-\d{2}[a-z]?)/);
    if (m) runStatus[m[1]] = j.status; // passed | failed | broken
  });
} catch (e) { /* allure yoksa bos gecilir */ }

// ---------------------------------------------------------------- sabitler
const FOLDER_META = {
  '01': { fr: 'FR-001', ad: 'Sistem Girisi',                  component: 'Kimlik Dogrulama',
          onKosul: 'Keycloak crm realm\'inde CRM_AGENT rolune sahip test kullanicisi tanimli olmalidir.' },
  '02': { fr: 'FR-002', ad: 'Musteri Arama ve Goruntuleme',   component: 'Musteri Arama',
          onKosul: 'Gecerli oturum token\'i; Musteri A ve B (ayni soyad) ile pasiflestirilmis Musteri C fixture\'lari olusturulmus olmalidir.' },
  '03': { fr: 'FR-003', ad: 'Musteri Olusturma',              component: 'Musteri Olusturma',
          onKosul: 'Gecerli oturum token\'i; cityId ve genderId lookup servisinden cozulmus olmalidir.' },
  '04': { fr: 'FR-004', ad: 'Musteri Bilgilerini Guncelleme', component: 'Musteri Guncelleme',
          onKosul: 'Gecerli oturum token\'i; Musteri A olusturulmus olmalidir.' },
  '05': { fr: 'FR-005', ad: 'Adres Yonetimi',                 component: 'Adres Yonetimi',
          onKosul: 'Gecerli oturum token\'i; tek adresli Musteri D olusturulmus olmalidir.' },
  '06': { fr: 'FR-006', ad: 'Iletisim Bilgileri Yonetimi',    component: 'Iletisim Bilgileri',
          onKosul: 'Gecerli oturum token\'i; Musteri E olusturulmus ve onboarding sirasinda e-posta, GSM, ev telefonu ve faks bilgileri girilmis olmalidir.' },
  '07': { fr: 'FR-007', ad: 'Musteri Silme',                  component: 'Musteri Silme',
          onKosul: 'Gecerli oturum token\'i; Musteri G olusturulmus ve tek adresi bulunuyor olmalidir.' },
  '08': { fr: 'FR-008', ad: 'Fatura Hesabi Olusturma',        component: 'Fatura Hesabi',
          onKosul: 'Gecerli oturum token\'i; Musteri F olusturulmus, varsayilan hesabi ve adres id\'si okunmus olmalidir. IDOR testleri icin Musteri E\'nin adres id\'si de gereklidir.' },
  '09': { fr: 'FR-009', ad: 'Fatura Hesabi ve Bagli Urun Goruntuleme', component: 'Fatura Hesabi',
          onKosul: 'Gecerli oturum token\'i; Musteri F uzerinde FR-008 testleriyle en az iki fatura hesabi acilmis olmalidir. Urun uclari icin order-service ayakta olmalidir.' },
  '10': { fr: 'FR-010', ad: 'Fatura Hesabi Guncelleme',       component: 'Fatura Hesabi',
          onKosul: 'Gecerli oturum token\'i; Musteri F uzerinde guncellenecek bir fatura hesabi ve en az iki adresi bulunmalidir.' },
  '11': { fr: 'FR-011', ad: 'Fatura Hesabi Silme',            component: 'Fatura Hesabi',
          onKosul: 'Gecerli oturum token\'i; Musteri F uzerinde silinecek aktif bir fatura hesabi ve onboarding varsayilan hesabi bulunmalidir.' }
};

// Scriptten otomatik cikarilamayan (dinamik baslikli / dongulu) senaryolar icin elle tanim.
const OVERRIDE = {
  'TC-005-15': {
    result: 'Musterinin adres sayisi 5 olana kadar her ekleme HTTP 201 doner; her ekleme ayri bir '
          + 'dogrulama olarak raporlanir.'
  },
  'TC-001-18': { resultPrefix: 'HTTP 4xx' },
  'TC-002-20': { resultPrefix: 'HTTP 200 (istek islenir)' },
  'TC-001-13': { resultPrefix: 'HTTP 200, 400 veya 401 (davranis belgelenir)' },
  'TC-007-06': {
    action: 'GET /api/v1/orders?custAcctId={Musteri G fatura hesabi id}',
    result: 'HTTP 200 ve bos urun listesi doner (silme on kosulu saglanir). Ayrica "bagli urun guard\'i '
          + 'order-service ile entegre" maddesi bekleyen boslik olarak raporlanir.'
  },
  'TC-009-04': {
    result: 'Musterinin hesap sayisi 6 olana kadar her ekleme HTTP 201 doner; her ekleme ayri bir '
          + 'dogrulama olarak raporlanir.'
  },
  'TC-011-10': {
    action: 'GET /api/v1/orders?custAcctId={Musteri F fatura hesabi id}',
    result: 'HTTP 200 ve bos urun listesi doner (silme on kosulu saglanir). Ayrica "bagli urun guard\'i '
          + 'order-service ile entegre" maddesi bekleyen boslik olarak raporlanir.'
  },
  'TC-009-10': {
    action: 'GET /api/v1/orders?custAcctId={Musteri F fatura hesabi id} - yanit alanlari ACC-007 '
          + 'modal alanlariyla karsilastirilir',
    result: 'Product Offer Name, Product Offer ID, Product Spec ID, Service Start Date, Prod Chars ve '
          + 'Service Address alanlari yanitta bulunmalidir. Su an bulunmuyor - bekleyen boslik.'
  }
};

// Test ID -> bulunan kusur eslemesi (izlenebilirlik matrisinden)
const BULGU = {
  'TC-001-04': 'B-01', 'TC-001-05': 'B-01', 'TC-001-10': 'B-01', 'TC-001-18': 'B-01',
  'TC-004-06': 'B-03', 'TC-003-29': 'B-06', 'TC-005-20': 'B-07',
  'TC-009-12': 'B-10', 'TC-009-13': 'B-10',
  'TC-009-11': 'B-11', 'TC-007-10': 'B-11',
  'TC-008-12': 'B-12', 'TC-010-11': 'B-12',
  'TC-008-11': 'B-13', 'TC-011-05': 'B-13', 'TC-006-10': 'B-13', 'TC-006-23': 'B-13',
  'TC-008-20': 'B-14'
};

// ---------------------------------------------------------------- yardimcilar
const oneLine = (s) => String(s == null ? '' : s).replace(/\s*\r?\n\s*/g, ' ').replace(/\s{2,}/g, ' ').trim();

function scriptOf(item, listen) {
  const ev = (item.event || []).find(e => e.listen === listen);
  return ev ? ev.script.exec.join('\n') : '';
}

/** Postman degisken/yardimci ifadelerini okunabilir metne cevirir. */
const OKUNUR = [
  [/pm\.environment\.get\('username'\)/g, '<test kullanicisi>'],
  [/pm\.environment\.get\('password'\)/g, '<gecerli parola>'],
  [/Number\(pm\.collectionVariables\.get\('cityId'\)\)/g, '<gecerli sehir id>'],
  [/Number\(pm\.collectionVariables\.get\('genderIdFemale'\)\)/g, '<cinsiyet id: Kadin>'],
  [/Number\(pm\.collectionVariables\.get\('genderId'\)\)/g, '<cinsiyet id: Erkek>'],
  [/pm\.collectionVariables\.get\('tcknA'\)/g, '<Musteri A T.C. no>'],
  [/pm\.collectionVariables\.get\('tcknB'\)/g, '<Musteri B T.C. no>'],
  [/pm\.collectionVariables\.get\('tcknC'\)/g, '<Musteri C T.C. no (pasif)>'],
  [/pm\.collectionVariables\.get\('sagaTckn'\)/g, '<yeni olusturulan musterinin T.C. no>'],
  [/pm\.collectionVariables\.get\('updatedLastNameA'\)/g, '<Musteri A guncel soyadi>'],
  [/pm\.collectionVariables\.get\('firstNameA'\)/g, '<Musteri A adi>'],
  [/pm\.collectionVariables\.get\('lastNameA'\)/g, '<Musteri A soyadi>'],
  [/newTckn\(\)/g, '<gecerli T.C. no uretilir>'],
  [/newBillingAddress\(\)/g, '<yeni adres: gecerli sehir id, "Fatura Sokak", "No:7 D:2", "Fatura adresi">'],
  [/Number\(pm\.collectionVariables\.get\('addressIdE1'\)\)/g, '<Musteri E adres id>'],
  [/Number\(pm\.collectionVariables\.get\('addressIdF2'\)\)/g, '<Musteri F 2. adres id>'],
  [/Number\(pm\.collectionVariables\.get\('addressIdF3'\)\)/g, '<Musteri F 3. adres id>'],
  [/pm\.collectionVariables\.get\('emailEYeni'\)/g, '<Musteri E guncel e-posta>'],
  [/pm\.collectionVariables\.get\('gsmEYeni'\)/g, '<Musteri E guncel GSM>'],
  [/newGsm\(\)/g, '<gecerli GSM uretilir>'],
  [/uniq\(\)/g, '<tekil ek>'],
  [/repeat\('([^']*)', ?(\d+)\)/g, '$2 karakterlik metin'],
  [/dateOffset\((\d+)\)/g, 'bugunden $1 gun sonraki tarih'],
  [/' \+ Date\.now\(\)/g, "-<zaman damgasi>'"],
  [/\bb\.(individual|contact|addresses\[0\])\./g, '$1.'],
  [/\bb\./g, ''],
  [/;\s*$/, '']
];

/** {{degisken}} yer tutucularini okunur hale getirir. */
const YER_TUTUCU = {
  custIdA: '{Musteri A id}', custIdB: '{Musteri B id}', custIdC: '{Musteri C id}',
  custIdD: '{Musteri D id}', custIdSaga: '{yeni olusturulan musteri id}',
  custIdE: '{Musteri E id}', custIdF: '{Musteri F id}', custIdG: '{Musteri G id}',
  addressIdE1: '{Musteri E adres id}', addressIdF1: '{Musteri F 1. adres id}',
  addressIdF2: '{Musteri F 2. adres id}', addressIdF3: '{Musteri F 3. adres id}',
  addressIdG1: '{Musteri G adres id}',
  acctIdF: '{Musteri F fatura hesabi id}', acctIdF2: '{Musteri F 2. fatura hesabi id}',
  acctIdG: '{Musteri G fatura hesabi id}',
  defaultAcctIdF: '{Musteri F varsayilan (CUST_ACCT) hesap id}',
  gAccountBody: '{ "accountName": "<tekil ad>", "accountDesc": "FR-007 on kosul hesabi", "addressId": "{Musteri G adres id}" }',
  tcknA: '{Musteri A T.C. no}', tcknB: '{Musteri B T.C. no}', tcknC: '{Musteri C T.C. no}',
  sagaTckn: '{yeni musteri T.C. no}',
  firstNameA: '{Musteri A adi}', lastNameA: '{Musteri A soyadi}',
  gsmA: '{Musteri A GSM}', acctNoA: '{Musteri A hesap no}',
  addressIdD1: '{Musteri D 1. adres id}', addressIdD2: '{Musteri D 2. adres id}',
  prefixA: '{Musteri A adinin ilk 8 karakteri}',
  lowerA: '{Musteri A adi - kucuk harf}', upperA: '{Musteri A adi - BUYUK harf}',
  partialTckn: '{T.C. no ilk 5 hanesi}',
  refreshBody: '{ "refreshToken": "<gecerli refresh token>" }',
  logoutBody: '{ "refreshToken": "<sonlandirilan oturumun refresh token\'i>" }'
};

function okunurYap(s) {
  let out = String(s || '');
  OKUNUR.forEach(([re, rep]) => { out = out.replace(re, rep); });
  out = out.replace(/\{\{([a-zA-Z0-9_]+)\}\}/g, (m, v) => YER_TUTUCU[v] || '{' + v + '}');
  return out;
}

/** Pre-request icindeki govde tanimini veya mutasyonunu cikarir. */
function extractMutation(pre) {
  if (!pre) return { tip: '', icerik: '' };
  const lines = pre.split('\n');

  // 1) Hazir govde uzerinde degisiklik: var b = newOnboardBody(); ...
  const mutIdx = lines.findIndex(l => /var b = (newOnboardBody|newAddressBody|currentIndividualBody|newContactBody|newBillingBody)\(/.test(l));
  if (mutIdx !== -1) {
    const out = [];
    for (let i = mutIdx + 1; i < lines.length; i++) {
      const l = lines[i].trim();
      if (l.startsWith('pm.collectionVariables.set(')) break;
      if (!l || l.startsWith('//')) continue;
      out.push(l);
    }
    return { tip: 'mutasyon', icerik: okunurYap(out.join(' ')) };
  }

  // 2) Dogrudan govde tanimi: var b = { ... };
  const litIdx = lines.findIndex(l => /var b = \{/.test(l));
  if (litIdx !== -1) {
    const out = [];
    for (let i = litIdx; i < lines.length; i++) {
      const l = lines[i].trim();
      if (l.startsWith('pm.collectionVariables.set(')) break;
      out.push(l);
    }
    const govde = out.join(' ').replace(/^var b = /, '').replace(/;\s*$/, '');
    return { tip: 'govde', icerik: okunurYap(govde) };
  }

  // 3) Govde dogrudan degiskene yaziliyor (statusCase):
  //    pm.collectionVariables.set('st_TC_011_06', JSON.stringify({ status: "PASSIVE" }));
  const setLit = pre.match(/pm\.collectionVariables\.set\('[a-zA-Z0-9_]+',\s*JSON\.stringify\((\{[^;]*\})\)\)/);
  if (setLit) {
    return { tip: 'govde', icerik: okunurYap(setLit[1].trim()) };
  }

  return { tip: '', icerik: '' };
}

/** Test scriptinden beklenen statu ve mesajlari cikarir. */
function extractExpectation(test) {
  const codes = [];
  const reCode = /expectCode\(([^)]*)\)/g;
  let m;
  while ((m = reCode.exec(test))) {
    m[1].split(',').map(s => s.trim()).filter(s => /^\d+$/.test(s)).forEach(c => {
      if (!codes.includes(c)) codes.push(c);
    });
  }
  const msgs = [];
  const reMsg = /expectMessage\('((?:[^'\\]|\\.)*)'\)/g;
  while ((m = reMsg.exec(test))) msgs.push(m[1].replace(/\\'/g, "'"));
  const reAny = /expectMessageAny\(([^)]*)\)/g;
  while ((m = reAny.exec(test))) {
    const parts = m[1].match(/'((?:[^'\\]|\\.)*)'/g) || [];
    msgs.push(parts.map(p => p.slice(1, -1).replace(/\\'/g, "'")).join(' VEYA '));
  }
  return { codes, msgs };
}

/** Ek dogrulamalari (pm.test basliklari) toplar - beklenen sonuca zenginlik katar. */
function extractAssertionTitles(test) {
  const titles = [];
  const re = /\b(?:pm\.test|gapTest)\('((?:[^'\\]|\\.)*)'/g;
  let m;
  while ((m = re.exec(test))) {
    const t = m[1].replace(/\\'/g, "'");
    if (!/^\[ORTAK\]/.test(t)) titles.push(t);
  }
  return titles;
}

function testTipi(name, codes, isGap) {
  if (/IDOR|token|Bozuk JWT|yetkisiz/i.test(name)) return 'Guvenlik';
  if (/BVA|sinir/i.test(name)) return 'Sinir Deger (BVA)';
  if (isGap) return 'Negatif (dokuman kurali)';
  const first = codes[0] ? Number(codes[0]) : 0;
  if (first >= 400) return 'Negatif';
  return 'Pozitif';
}

// ---------------------------------------------------------------- satirlari uret
const rows = [];

col.item.forEach(folder => {
  const key = folder.name.slice(0, 2);
  const meta = FOLDER_META[key];
  if (!meta) return; // 00 Setup ve 99 Teardown haric

  folder.item.forEach(item => {
    const name = item.name;
    const idMatch = name.match(/^(TC-\d{3}-\d{2}[a-z]?)\s*·\s*(.*)$/);
    if (!idMatch) return;
    const tcid = idMatch[1];
    const baslik = idMatch[2];

    const req = item.request;
    const method = req.method || 'GET';
    const rawUrl = (req.url && req.url.raw ? req.url.raw : '').replace('{{baseUrl}}', '');

    const pre = scriptOf(item, 'prerequest');
    const test = scriptOf(item, 'test');
    const isGap = /gapTest\(/.test(test);

    const { codes, msgs } = extractExpectation(test);
    const mutation = extractMutation(pre);

    // --- Action: gercekte atilan HTTP istegi
    // Bazi senaryolar dongulu calisir; istek /actuator/health'e atilir, asil cagrilar test
    // script'inden pm.sendRequest ile yapilir. Bunlarin adimi elle tanimlanir (bkz. TASIYICI).
    const TASIYICI = {
      'TC-005-15': {
        action: 'Adres limiti dolana kadar art arda POST /api/v1/customers/{Musteri D id}/addresses istegi gonderilir.',
        data: 'Her istekte gecerli adres govdesi (sehir, sokak, bina no, aciklama) gonderilir.'
      },
      'TC-009-04': {
        action: 'Hesap sayisi 6 olana kadar art arda POST /api/v1/customers/{Musteri F id}/accounts istegi gonderilir.',
        data: 'Her istekte gecerli fatura hesabi govdesi (hesap adi, aciklama, mevcut adres id) gonderilir.'
      }
    };
    const tasiyici = rawUrl === '/actuator/health' ? (TASIYICI[tcid] || null) : null;
    const ovAction = (OVERRIDE[tcid] || {}).action;
    const action = ovAction ? ovAction
      : tasiyici ? tasiyici.action
      : okunurYap(method + ' ' + rawUrl);

    // --- Data: istek verisi
    let data = '';
    if (tasiyici) {
      data = tasiyici.data;
    } else if (mutation.tip === 'mutasyon') {
      data = mutation.icerik
        ? 'Gecerli govde uzerinde degisiklik: ' + mutation.icerik
        : 'Gecerli govde kullanilir (ek degisiklik yapilmaz).';
    } else if (mutation.tip === 'govde') {
      data = 'Govde: ' + mutation.icerik;
    } else if (rawUrl.includes('?')) {
      data = 'Query: ' + okunurYap(rawUrl.split('?')[1]);
    } else if (req.body && req.body.raw) {
      data = 'Govde: ' + okunurYap(req.body.raw);
    } else {
      data = 'Govde yok.';
    }

    // --- Result: beklenen sonuc
    const parcalar = [];
    if (codes.length) parcalar.push('HTTP ' + codes.join(' veya '));
    msgs.forEach(m2 => parcalar.push('Mesaj: "' + m2 + '"'));
    const digerDogrulamalar = extractAssertionTitles(test)
      .map(t => t.replace(/^TC-\d{3}-\d{2}[a-z]?\s*·\s*/, ''))
      .filter(t => t !== baslik && t !== baslik.replace(/\s*\[[^\]]*\]\s*$/, ''));
    if (digerDogrulamalar.length) parcalar.push('Ek dogrulama: ' + digerDogrulamalar.join(' / '));
    const ov = OVERRIDE[tcid] || {};
    if (ov.resultPrefix) parcalar.unshift(ov.resultPrefix);
    let result = ov.result ? ov.result : parcalar.join(' | ');
    if (!result) result = 'Istek basariyla islenir.';
    if (isGap) {
      result += ' | NOT: Bu kural gereksinim dokumaninda tanimli ancak API\'de karsiligi yok; '
             + 'otomasyonda "bekleyen boslik" olarak izlenir.';
    }

    // --- ACC referanslari
    const accMatch = baslik.match(/\[([^\]]*ACC[^\]]*)\]/);
    const acc = accMatch ? accMatch[1] : '';

    // --- Aciklama
    const aciklamaParcalari = [
      'Gereksinim: ' + meta.fr + ' - ' + meta.ad + (acc ? ' (' + acc + ')' : ''),
      'On kosul: ' + meta.onKosul
    ];
    if (item.request.description) aciklamaParcalari.push('Not: ' + oneLine(item.request.description));
    if (BULGU[tcid]) aciklamaParcalari.push('Iliskili bulgu: ' + BULGU[tcid]);
    if (tcid === 'TC-003-41') aciklamaParcalari.push('Bu senaryo icin gereksinim dokumaninda kabul kriteri yoktur - acik analiz sorusu olarak izlenmektedir.');
    aciklamaParcalari.push('Otomasyon: Postman/Newman koleksiyonu "' + folder.name + '" klasoru, istek adi "' + name + '".');

    // --- Etiketler
    const tip = testTipi(name, codes, isGap);
    const labels = [meta.fr, 'otomasyon', 'postman', 'api-testi'];
    if (tip === 'Guvenlik') labels.push('guvenlik');
    if (tip === 'Sinir Deger (BVA)') labels.push('bva');
    if (tip.startsWith('Negatif')) labels.push('negatif');
    if (tip === 'Pozitif') labels.push('pozitif');
    if (isGap) labels.push('bekleyen-boslik');
    if (BULGU[tcid]) labels.push('bulgu-' + BULGU[tcid].replace('-', '').toLowerCase());

    // --- Oncelik
    let priority = 'Medium';
    if (BULGU[tcid]) priority = 'High';
    else if (/Gecerli kimlikle giris|Gecerli kayit|Saga kaniti|tam eslesme|Adresler listelenir|Gecerli guncelleme/i.test(baslik)) priority = 'High';
    else if (isGap) priority = 'Low';

    // --- Son kosum
    const st = runStatus[tcid];
    const sonKosum = st === 'passed' ? 'Gecti' : (st ? 'Kaldi' : 'Kosulmadi');

    rows.push({
      TCID: tcid,
      Summary: name,
      Description: aciklamaParcalari.join(' || '),
      Action: oneLine(action),
      Data: oneLine(data).slice(0, 900),
      Result: oneLine(result).slice(0, 900),
      'Test Type': 'Manual',
      Priority: priority,
      Labels: labels.join(' '),
      Component: meta.component,
      'Test Repository Path': 'CRM Lite/' + meta.fr + ' ' + meta.ad,
      'Test Tipi': tip,
      'Son Kosum': sonKosum,
      'Iliskili Bulgu': BULGU[tcid] || ''
    });
  });
});

// ---------------------------------------------------------------- CSV yaz
const HEADERS = ['TCID', 'Summary', 'Description', 'Action', 'Data', 'Result', 'Test Type',
                 'Priority', 'Labels', 'Component', 'Test Repository Path',
                 'Test Tipi', 'Son Kosum', 'Iliskili Bulgu'];

function csvCell(v) {
  const s = String(v == null ? '' : v);
  return /[",\n\r;]/.test(s) ? '"' + s.replace(/"/g, '""') + '"' : s;
}
function toCsv(rows, delim) {
  const lines = [HEADERS.map(csvCell).join(delim)];
  rows.forEach(r => lines.push(HEADERS.map(h => csvCell(r[h])).join(delim)));
  return lines.join('\r\n') + '\r\n';
}

fs.mkdirSync(OUT_DIR, { recursive: true });

// Xray icin: UTF-8 (BOM YOK), virgul ayirici
fs.writeFileSync(path.join(OUT_DIR, 'CRM-Lite-FR001-FR011-xray.csv'), toCsv(rows, ','), 'utf8');
// Excel'de gozle kontrol icin: UTF-8 BOM'lu, noktali virgul ayirici
fs.writeFileSync(path.join(OUT_DIR, 'CRM-Lite-FR001-FR011-excel.csv'), '\ufeff' + toCsv(rows, ';'), 'utf8');

// ---------------------------------------------------------------- ozet
console.log('Toplam test senaryosu: ' + rows.length);
const byFr = {};
rows.forEach(r => { const fr = r.Labels.split(' ')[0]; byFr[fr] = (byFr[fr] || 0) + 1; });
Object.keys(byFr).sort().forEach(k => console.log('  ' + k + ': ' + byFr[k]));
const byTip = {};
rows.forEach(r => { byTip[r['Test Tipi']] = (byTip[r['Test Tipi']] || 0) + 1; });
console.log('--- test tipi dagilimi ---');
Object.keys(byTip).sort().forEach(k => console.log('  ' + k + ': ' + byTip[k]));
const byStatus = {};
rows.forEach(r => { byStatus[r['Son Kosum']] = (byStatus[r['Son Kosum']] || 0) + 1; });
console.log('--- son kosum ---');
Object.keys(byStatus).sort().forEach(k => console.log('  ' + k + ': ' + byStatus[k]));
console.log('--- bulguya bagli ---');
console.log('  ' + rows.filter(r => r['Iliskili Bulgu']).map(r => r.TCID + '=' + r['Iliskili Bulgu']).join(', '));

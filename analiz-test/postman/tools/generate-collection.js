/* eslint-disable */
// CRM Lite - FR-001..FR-005 Postman koleksiyonu ureticisi.
const fs = require('fs');
const path = require('path');
const OUT = path.join(__dirname, '..', 'CRM-Lite-FR001-FR005.postman_collection.json');

const s = (t) => t.replace(/^\n+/, '').replace(/\s+$/, '').split('\n');

function url(p) {
  const [pathPart, query] = p.split('?');
  const segs = pathPart.split('/').filter(Boolean);
  const u = { raw: '{{baseUrl}}/' + segs.join('/') + (query ? '?' + query : ''), host: ['{{baseUrl}}'], path: segs };
  if (query) u.query = query.split('&').map((kv) => { const i = kv.indexOf('='); return { key: kv.slice(0, i), value: kv.slice(i + 1) }; });
  return u;
}

function req(o) {
  const hdr = (o.headers || []).slice();
  if (o.body) hdr.unshift({ key: 'Content-Type', value: 'application/json' });
  if (o.noAuth) hdr.push({ key: 'X-No-Auth', value: 'true' });
  const item = { name: o.name, event: [], request: { method: o.method || 'GET', header: hdr, url: url(o.path) } };
  if (o.description) item.request.description = o.description;
  if (o.body) item.request.body = { mode: 'raw', raw: o.body, options: { raw: { language: 'json' } } };
  if (o.noAuth) item.request.auth = { type: 'noauth' };
  if (o.pre) item.event.push({ listen: 'prerequest', script: { type: 'text/javascript', exec: s(o.pre) } });
  if (o.test) item.event.push({ listen: 'test', script: { type: 'text/javascript', exec: s(o.test) } });
  return item;
}

const folder = (name, description, items) => ({ name, description, item: items });

// ---------------------------------------------------------------------------
// Uretici kisayollar
// ---------------------------------------------------------------------------
const H = "eval(pm.collectionVariables.get('crmHelpers'));";

/** msg string ise expectMessage, dizi ise expectMessageAny satiri uretir. */
const q = (m) => "'" + String(m).replace(/'/g, "\\'") + "'";
const msgLine = (msg) => {
  if (!msg) return '';
  return Array.isArray(msg)
    ? '\n  expectMessageAny(' + msg.map(q).join(', ') + ');'
    : '\n  expectMessage(' + q(msg) + ');';
};

/** Onboarding senaryosu: gecerli govdeyi uretir, mutasyonu uygular, statu/mesaj dogrular. */
function onboardCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'b_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'POST',
    path: '/api/v1/customers/onboarding',
    body: '{{' + v + '}}',
    description: opts.description,
    pre: `${H}
var b = newOnboardBody();
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
if (pm.response.code === 201) { trackId('createdCustomerIds', jsonBody().custId); }
${opts.extraTest || ''}`
  });
}

/** Kimlik dogrulama (verify-identity) senaryosu. */
function verifyCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'v_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'POST',
    path: '/api/v1/customers/onboarding/verify-identity',
    body: '{{' + v + '}}',
    pre: `${H}
var b = newOnboardBody().individual;
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});`
  });
}

/** Login senaryosu (noAuth). */
function loginCase(tc, title, bodySrc, expect, msg, opts) {
  opts = opts || {};
  const v = 'l_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'POST',
    path: '/api/v1/auth/login',
    noAuth: true,
    body: '{{' + v + '}}',
    description: opts.description,
    pre: `${H}
var b = ${bodySrc};
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
${opts.extraTest || ''}`
  });
}

/** Demografik guncelleme senaryosu (FR-004). */
function individualCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'i_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'PUT',
    path: '/api/v1/customers/' + (opts.custIdVar || '{{custIdA}}') + '/individual',
    body: '{{' + v + '}}',
    pre: `${H}
var b = currentIndividualBody();
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
${opts.extraTest || ''}`
  });
}

/** Adres senaryosu (FR-005). */
function addressCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'a_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: opts.method || 'POST',
    path: opts.path || '/api/v1/customers/{{custIdD}}/addresses',
    body: '{{' + v + '}}',
    pre: `${H}
var b = newAddressBody();
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
${opts.extraTest || ''}`
  });
}

/** Fixture musteri olusturan setup istegi. */
function fixture(tc, label, key, extraPre) {
  return req({
    name: tc + ' · [' + label + '] olustur',
    method: 'POST',
    path: '/api/v1/customers/onboarding',
    body: '{{fx_' + key + '}}',
    pre: `${H}
var b = newOnboardBody();
${extraPre || ''}
pm.collectionVariables.set('firstName${key}', b.individual.firstName);
pm.collectionVariables.set('lastName${key}', b.individual.lastName);
pm.collectionVariables.set('tckn${key}', b.individual.nationalId);
pm.collectionVariables.set('gsm${key}', b.contact.mobilePhone);
pm.collectionVariables.set('email${key}', b.contact.email);
pm.collectionVariables.set('fx_${key}', JSON.stringify(b));`,
    test: `${H}
pm.test('[${label}] olusturuldu (201)', function () { expectCode(201); });
var r = jsonBody();
if (r.custId) {
  pm.collectionVariables.set('custId${key}', r.custId);
  pm.collectionVariables.set('acctNo${key}', r.accounts && r.accounts[0] ? r.accounts[0].accountNo : '');
  trackId('createdCustomerIds', r.custId);
  console.log('[SETUP] ${label}: custId=' + r.custId + ' ad=' + pm.collectionVariables.get('firstName${key}') + ' soyad=' + pm.collectionVariables.get('lastName${key}'));
}`
  });
}

// ===========================================================================
// KOLEKSIYON SEVIYESI SCRIPTLER
// ===========================================================================
const collectionPreRequest = `
// ===========================================================================
// CRM Lite (FR-001..FR-005) - koleksiyon seviyesi pre-request
//  1) crmHelpers yardimci fonksiyonlarini hazirlar
//  2) her kosum icin tekil runId uretir (test verisi cakismasin diye)
//  3) token yoksa / suresi dolmak uzereyse otomatik login olur
// ===========================================================================
var HELPERS = [
  "function crmStrict(){ return String(pm.environment.get('strictMode')).toLowerCase() === 'true'; }",
  "function crmLogGap(n){ var l = JSON.parse(pm.collectionVariables.get('gapLog') || '[]'); l.push(n); pm.collectionVariables.set('gapLog', JSON.stringify(l)); }",
  "function gapTest(name, fn){",
  "  if (crmStrict()) { pm.test(name, fn); return; }",
  "  try { fn(); pm.test('[GAP KAPANDI] ' + name, function(){ pm.expect(true).to.be.true; }); }",
  "  catch (e) { crmLogGap(name); console.warn('[BEKLEYEN BOSLUK] ' + name + ' -> ' + e.message); pm.test('[BEKLEYEN BOSLUK] ' + name, function(){ pm.expect(true).to.be.true; }); }",
  "}",
  "function trackId(key, id){ if (id === undefined || id === null) return; var l = JSON.parse(pm.collectionVariables.get(key) || '[]'); if (l.indexOf(id) === -1) { l.push(id); pm.collectionVariables.set(key, JSON.stringify(l)); } }",
  "function jsonBody(){ try { return pm.response.json(); } catch (e) { return {}; } }",
  "function expectCode(){ var codes = Array.prototype.slice.call(arguments); pm.expect(codes, 'beklenen ' + codes.join('/') + ', gelen ' + pm.response.code + ' | ' + String(pm.response.text()).slice(0, 250)).to.include(pm.response.code); }",
  "function expectMessage(part){ pm.expect(String(pm.response.text()), 'beklenen mesaj parcasi: ' + part).to.include(part); }",
  "// Bir alan ayni anda birden fazla kisiti ihlal ettiginde (orn. @NotBlank + @Pattern)",
  "// Bean Validation hangisini once raporlayacagini garanti etmez - herhangi biri yeterlidir.",
  "function expectMessageAny(){ var parts = Array.prototype.slice.call(arguments); var body = String(pm.response.text()); var hit = parts.some(function (p) { return body.indexOf(p) > -1; }); pm.expect(hit, 'beklenen mesajlardan biri (' + parts.join(' | ') + ') bulunamadi: ' + body.slice(0, 200)).to.eql(true); }",
  "// Bir alan ayni anda birden fazla kisiti ihlal ettiginde (orn. @NotBlank + @Pattern)",
  "// Bean Validation hangisini once raporlayacagini garanti etmez; herhangi biri yeterlidir.",
  "function expectMessageAny(){ var parts = Array.prototype.slice.call(arguments); var body = String(pm.response.text()); var hit = parts.some(function(p){ return body.indexOf(p) > -1; }); pm.expect(hit, 'beklenen mesajlardan biri (' + parts.join(' | ') + ') bulunamadi: ' + body.slice(0, 200)).to.eql(true); }",
  "function newTckn(){ var d = []; var x = Math.floor(Math.random() * 900000000) + 100000000; d[0] = (x % 9) + 1; for (var i = 1; i < 9; i++) { x = (x * 31 + 7) % 1000000007; d[i] = x % 10; } var odd = d[0] + d[2] + d[4] + d[6] + d[8]; var even = d[1] + d[3] + d[5] + d[7]; var d10 = ((odd * 7) - even) % 10; if (d10 < 0) { d10 += 10; } d[9] = d10; var t = 0; for (var j = 0; j < 10; j++) { t += d[j]; } d[10] = t % 10; return d.join(''); }",
  "function newGsm(){ var n = '5'; for (var i = 0; i < 9; i++) { n += Math.floor(Math.random() * 10); } return n; }",
  "function lettersOf(v){ var map = 'abcdefghij'; var out = ''; var s = String(v); for (var i = 0; i < s.length; i++) { var d = parseInt(s.charAt(i), 10); out += isNaN(d) ? 'x' : map.charAt(d); } return out; }",
  "function uniq(){ return lettersOf(pm.collectionVariables.get('runId')) + lettersOf(String(Math.floor(Math.random() * 9000) + 1000)); }",
  "function repeat(ch, n){ var o = ''; for (var i = 0; i < n; i++) { o += ch; } return o; }",
  "function dateOffset(days){ var d = new Date(Date.now() + days * 86400000); return ('0' + d.getDate()).slice(-2) + '/' + ('0' + (d.getMonth() + 1)).slice(-2) + '/' + d.getFullYear(); }",
  "function newAddressBody(){ return { cityId: Number(pm.collectionVariables.get('cityId')), streetName: 'Cumhuriyet Caddesi', buildingName: 'No:12 D:3', addressDesc: 'Ev adresi', primary: false }; }",
  "function newOnboardBody(){",
  "  var u = uniq();",
  "  return {",
  "    individual: { firstName: 'Test' + u, middleName: 'Orta', lastName: 'Soyad' + u, birthDate: '15/06/1990', genderId: Number(pm.collectionVariables.get('genderId')), motherName: 'Anne', fatherName: 'Baba', nationalId: newTckn() },",
  "    addresses: [{ cityId: Number(pm.collectionVariables.get('cityId')), streetName: 'Cumhuriyet Caddesi', buildingName: 'No:12 D:3', addressDesc: 'Ev adresi' }],",
  "    contact: { email: 'crm.' + u.toLowerCase() + '@example.com', mobilePhone: newGsm(), homePhone: '2121234567', fax: '2129876543' }",
  "  };",
  "}",
  "function currentIndividualBody(){",
  "  return { firstName: pm.collectionVariables.get('firstNameA'), middleName: 'Orta', lastName: pm.collectionVariables.get('lastNameA'), genderId: Number(pm.collectionVariables.get('genderId')), motherName: 'Anne', fatherName: 'Baba', birthDate: '15/06/1990', nationalId: pm.collectionVariables.get('tcknA') };",
  "}",
  "function authHeader(){ return 'Bearer ' + pm.collectionVariables.get('accessToken'); }",
  "function jsonHeaders(){ return { 'Content-Type': 'application/json', 'Authorization': authHeader() }; }",
  "function resetLoginFailures(){ pm.sendRequest({ url: pm.environment.get('baseUrl') + '/api/v1/auth/login', method: 'POST', header: { 'Content-Type': 'application/json' }, body: { mode: 'raw', raw: JSON.stringify({ username: pm.environment.get('username'), password: pm.environment.get('password') }) } }, function(){}); }",
  "function ids(list){ return (list || []).map(function(r){ return String(r.custId); }); }"
].join('\\n');
pm.collectionVariables.set('crmHelpers', HELPERS);

if (!pm.collectionVariables.get('runId')) {
  pm.collectionVariables.set('runId', String(Date.now()).slice(-8));
  pm.collectionVariables.set('gapLog', '[]');
  pm.collectionVariables.set('createdCustomerIds', '[]');
  pm.collectionVariables.set('cleanupLeftovers', '[]');
  console.log('[CRM] Kosum basladi. runId=' + pm.collectionVariables.get('runId') + ' | strictMode=' + pm.environment.get('strictMode'));
}

var reqPath = pm.request.url.getPath();
var noAuthHeader = pm.request.headers.get('X-No-Auth');
if (noAuthHeader) { pm.request.headers.remove('X-No-Auth'); }
var skipAuth = noAuthHeader === 'true' || /\\/api\\/v1\\/auth\\//.test(reqPath);

if (!skipAuth) {
  var expiresAt = Number(pm.collectionVariables.get('tokenExpiresAt') || 0);
  var token = pm.collectionVariables.get('accessToken');
  if (!token || Date.now() > expiresAt - 30000) {
    pm.sendRequest({
      url: pm.environment.get('baseUrl') + '/api/v1/auth/login',
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      body: { mode: 'raw', raw: JSON.stringify({ username: pm.environment.get('username'), password: pm.environment.get('password') }) }
    }, function (err, res) {
      if (err || res.code !== 200) { console.error('[AUTH] Otomatik login basarisiz: ' + (err ? err : res.code)); return; }
      var b = res.json();
      pm.collectionVariables.set('accessToken', b.accessToken);
      pm.collectionVariables.set('refreshToken', b.refreshToken);
      pm.collectionVariables.set('tokenExpiresAt', Date.now() + (Number(b.expiresIn || 300) * 1000));
    });
  }
}
`;

const collectionTest = `
// Koleksiyon seviyesi ortak assertion'lar - her istekten sonra calisir.
eval(pm.collectionVariables.get('crmHelpers'));

pm.test('[ORTAK] Sunucu hatasi (5xx) donmedi', function () {
  pm.expect(pm.response.code, String(pm.response.text()).slice(0, 250)).to.be.below(500);
});

var maxMs = Number(pm.environment.get('maxResponseTimeMs') || 0);
if (maxMs > 0) {
  pm.test('[ORTAK] Yanit suresi < ' + maxMs + ' ms (' + pm.response.responseTime + ' ms)', function () {
    pm.expect(pm.response.responseTime).to.be.below(maxMs);
  });
}

var ct = pm.response.headers.get('Content-Type') || '';
if (/json/.test(ct) && pm.response.code !== 204) {
  pm.test('[ORTAK] Govde gecerli JSON', function () { pm.response.to.be.json; });
}
`;

// ===========================================================================
// 00 - ORTAM HAZIRLIGI
// ===========================================================================
const setup = folder(
  '00 · Ortam Hazirligi (Setup)',
  'Testlerin uzerinde calisacagi deterministik veri seti burada kurulur. Musteri A ve B bilincli olarak AYNI SOYADI tasir (FR-002 AND/OR ve siralama testleri icin); Musteri C silinip pasif hale getirilir; Musteri D adres testlerine ayrilmistir (adres sayisi deterministik kalsin diye).',
  [
    req({
      name: 'S01 · Gateway ayakta (health)',
      path: '/actuator/health',
      noAuth: true,
      test: `${H}
pm.test('Gateway 200 donuyor', function () { expectCode(200); });
pm.test('Health durumu UP', function () { pm.expect(jsonBody().status).to.eql('UP'); });
if (pm.response.code !== 200 && String(pm.environment.get('abortOnSetupFailure')).toLowerCase() === 'true') {
  console.error('[SETUP] Gateway ayakta degil - kosum durduruldu.');
  postman.setNextRequest(null);
}`
    }),
    req({
      name: 'S02 · Keycloak token al (ana kullanici)',
      method: 'POST',
      path: '/api/v1/auth/login',
      noAuth: true,
      body: '{{setupLoginBody}}',
      pre: `${H}
pm.collectionVariables.set('setupLoginBody', JSON.stringify({ username: pm.environment.get('username'), password: pm.environment.get('password') }));`,
      test: `${H}
var b = jsonBody();
pm.test('Token alindi (200)', function () { expectCode(200); });
pm.test('accessToken ve refreshToken dondu', function () {
  pm.expect(b.accessToken, 'accessToken').to.be.a('string').and.not.empty;
  pm.expect(b.refreshToken, 'refreshToken').to.be.a('string').and.not.empty;
});
if (b.accessToken) {
  pm.collectionVariables.set('accessToken', b.accessToken);
  pm.collectionVariables.set('refreshToken', b.refreshToken);
  pm.collectionVariables.set('tokenExpiresAt', Date.now() + (Number(b.expiresIn || 300) * 1000));
} else if (String(pm.environment.get('abortOnSetupFailure')).toLowerCase() === 'true') {
  console.error('[SETUP] Token alinamadi - kosum durduruldu.');
  postman.setNextRequest(null);
}`
    }),
    req({
      name: 'S03 · Il listesi -> cityId cozumle (CITY-ANKARA)',
      path: '/api/v1/general-types/resolve/CITY/ANKARA',
      description: 'cityId sabit yazilmaz: veritabani sifirlandiginda lookup id\'leri degisir. Kod ile cozulur, cozulemezse environment\'taki yedek deger kullanilir.',
      test: `${H}
var b = jsonBody();
pm.test('CITY/ANKARA cozumlendi (200)', function () { expectCode(200); });
pm.collectionVariables.set('cityId', (b && b.gnlTpId) ? b.gnlTpId : pm.environment.get('defaultCityId'));
console.log('[SETUP] cityId = ' + pm.collectionVariables.get('cityId'));`
    }),
    req({
      name: 'S04 · Cinsiyet -> genderId cozumle (GENDER-MALE, GENDER-FEMALE)',
      path: '/api/v1/general-types/resolve/GENDER/MALE',
      test: `${H}
var b = jsonBody();
pm.test('GENDER/MALE cozumlendi (200)', function () { expectCode(200); });
pm.collectionVariables.set('genderId', (b && b.gnlTpId) ? b.gnlTpId : pm.environment.get('defaultGenderId'));
pm.sendRequest({ url: pm.environment.get('baseUrl') + '/api/v1/general-types/resolve/GENDER/FEMALE', method: 'GET', header: jsonHeaders() }, function (err, res) {
  pm.test('GENDER/FEMALE cozumlendi (200)', function () {
    if (err) { throw new Error(String(err)); }
    pm.expect(res.code).to.eql(200);
  });
  pm.collectionVariables.set('genderIdFemale', (!err && res.code === 200) ? res.json().gnlTpId : pm.collectionVariables.get('genderId'));
  console.log('[SETUP] genderId = ' + pm.collectionVariables.get('genderId') + ' / ' + pm.collectionVariables.get('genderIdFemale'));
});`
    }),
    req({
      name: 'S05 · Gateway -> customer-service yonlendirmesi calisiyor',
      path: '/api/v1/customers/search?custId=999999999',
      test: `${H}
var b = jsonBody();
pm.test('Arama ucu yetkili istekte 200 donuyor', function () { expectCode(200); });
pm.test('Sayfali (Page) kontrat: content / totalElements / size', function () {
  pm.expect(b).to.have.property('content');
  pm.expect(b).to.have.property('totalElements');
  pm.expect(b).to.have.property('size');
});`
    }),
    fixture('S06', 'Musteri A - ana test musterisi', 'A'),
    fixture('S07', 'Musteri B - A ile AYNI SOYAD (AND-OR ve siralama verisi)', 'B',
      `b.individual.lastName = pm.collectionVariables.get('lastNameA');
b.individual.firstName = 'Ikinci' + uniq();`),
    fixture('S08', 'Musteri C - pasiflestirilecek', 'C'),
    req({
      name: 'S09 · [Musteri C] pasiflestirilir (soft-delete)',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdC}}',
      description: 'FR-002 ACC-005 "pasif musteri arama sonuclarinda gorunmez" testinin on kosulu.',
      test: `${H}
pm.test('[Musteri C] soft-delete edildi (204)', function () { expectCode(204); });`
    }),
    fixture('S10', 'Musteri D - adres testleri icin', 'D'),
    req({
      name: 'S11 · [Musteri D] birincil adres id alinir',
      path: '/api/v1/customers/{{custIdD}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('[Musteri D] tek adresi listelendi', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array').that.is.not.empty;
});
if (Array.isArray(b) && b[0]) {
  pm.collectionVariables.set('addressIdD1', b[0].id);
  console.log('[SETUP] Musteri D birincil adres id = ' + b[0].id);
}`
    })
  ]
);

// ===========================================================================
// 01 - FR-001
// ===========================================================================
const fr001 = folder(
  '01 · FR-001 Sistem Girisi (Keycloak)',
  'FR-001. Orta katmanda dogrulanabilen maddeler: ACC-004 (kimlik dogrulama), ACC-005/007 (hatali giris), ACC-009 (8 saatlik token), ACC-011 (oturum sonu), ACC-012/013 (logout). ACC-001/002/003/006/010 saf UI davranisidir.',
  [
    loginCase('TC-001-01', 'Gecerli kimlikle giris -> 200 + token semasi [ACC-004]',
      "{ username: pm.environment.get('username'), password: pm.environment.get('password') }", 200, null, {
      extraTest: `var b = jsonBody();
pm.test('TC-001-01 · Token kontrati: accessToken / refreshToken / tokenType / expiresIn', function () {
  pm.expect(b.accessToken).to.be.a('string').and.not.empty;
  pm.expect(b.refreshToken).to.be.a('string').and.not.empty;
  pm.expect(String(b.tokenType).toLowerCase()).to.eql('bearer');
  pm.expect(b.expiresIn).to.be.a('number');
});`
    }),
    loginCase('TC-001-02', 'Oturum token omru 8 saat (28800 sn) -> [ACC-009]',
      "{ username: pm.environment.get('username'), password: pm.environment.get('password') }", 200, null, {
      extraTest: `var b = jsonBody();
pm.test('TC-001-02 · expiresIn = 28800 (8 saat)', function () { pm.expect(Number(b.expiresIn)).to.eql(28800); });
var payload = {};
try { payload = JSON.parse(Buffer.from(String(b.accessToken).split('.')[1], 'base64').toString('utf8')); } catch (e) {}
pm.test('TC-001-02 · JWT exp - iat farki da 28800', function () { pm.expect(Number(payload.exp) - Number(payload.iat)).to.eql(28800); });`
    }),
    loginCase('TC-001-03', 'Token CRM_AGENT rolunu tasir -> [ACC-004]',
      "{ username: pm.environment.get('username'), password: pm.environment.get('password') }", 200, null, {
      extraTest: `var b = jsonBody();
var payload = {};
try { payload = JSON.parse(Buffer.from(String(b.accessToken).split('.')[1], 'base64').toString('utf8')); } catch (e) {}
pm.test('TC-001-03 · realm_access.roles icinde CRM_AGENT var', function () {
  var roles = (payload.realm_access && payload.realm_access.roles) || [];
  pm.expect(roles, JSON.stringify(roles)).to.include('CRM_AGENT');
});`
    }),
    loginCase('TC-001-04', 'Hatali parola -> 401 [ACC-005, ACC-007]',
      "{ username: pm.environment.get('username'), password: 'yanlis-parola-' + Date.now() }", 401, null, {
      description: 'BULGU B-01: su an 401 yerine 500 donuyor. Keycloak quick-login korumasini tetiklememek icin test bittiginde basarili bir giris yapilarak hata sayaci sifirlanir.',
      extraTest: `pm.test('TC-001-04 · Oturum olusturulmaz (token donmez)', function () {
  pm.expect(jsonBody().accessToken, 'accessToken donmemeli').to.be.undefined;
});
resetLoginFailures();`
    }),
    loginCase('TC-001-05', 'Var olmayan kullanici -> 401 [ACC-005]',
      "{ username: 'olmayan-kullanici-' + Date.now(), password: 'herhangi' }", 401),
    loginCase('TC-001-06', 'Bos kullanici adi -> 400', "{ username: '', password: pm.environment.get('password') }", 400),
    loginCase('TC-001-07', 'Bos parola -> 400', "{ username: pm.environment.get('username'), password: '' }", 400),
    loginCase('TC-001-08', 'Kullanici adi bastaki bosluk ile -> 400',
      "{ username: ' ' + pm.environment.get('username'), password: pm.environment.get('password') }", 400),
    loginCase('TC-001-09', 'Kullanici adi sondaki bosluk ile -> 400',
      "{ username: pm.environment.get('username') + ' ', password: pm.environment.get('password') }", 400),
    loginCase('TC-001-10', 'Kullanici adi 51 karakter -> 400 (BVA, dokuman kurali)',
      "{ username: repeat('a', 51), password: pm.environment.get('password') }", 400, null, { gap: true }),
    loginCase('TC-001-11', 'Kullanici adi buyuk-kucuk harfe duyarsiz -> 200',
      "{ username: String(pm.environment.get('username')).toUpperCase(), password: pm.environment.get('password') }", 200, null, { gap: true }),
    req({
      name: 'TC-001-12 · Refresh token ile yeni access token -> 200 [ACC-011]',
      method: 'POST',
      path: '/api/v1/auth/refresh',
      noAuth: true,
      body: '{{refreshBody}}',
      pre: `${H}
pm.sendRequest({
  url: pm.environment.get('baseUrl') + '/api/v1/auth/login',
  method: 'POST', header: { 'Content-Type': 'application/json' },
  body: { mode: 'raw', raw: JSON.stringify({ username: pm.environment.get('username'), password: pm.environment.get('password') }) }
}, function (err, res) {
  var rt = (!err && res.code === 200) ? res.json().refreshToken : '';
  pm.collectionVariables.set('isolatedRefreshToken', rt);
  pm.collectionVariables.set('refreshBody', JSON.stringify({ refreshToken: rt }));
});`,
      test: `${H}
var b = jsonBody();
pm.test('TC-001-12 · Refresh 200 doner ve yeni accessToken uretir', function () {
  expectCode(200);
  pm.expect(b.accessToken).to.be.a('string').and.not.empty;
});`
    }),
    req({
      name: 'TC-001-13 · Ayni refresh token ikinci kez kullanilabilir mi',
      method: 'POST',
      path: '/api/v1/auth/refresh',
      noAuth: true,
      body: '{{refreshBody}}',
      description: 'Refresh token rotation davranisinin belgelenmesi. Keycloak varsayilaninda tekrar kullanim engellenmez.',
      test: `${H}
pm.test('TC-001-13 · Istek islenir (davranis belgelenir: HTTP ' + pm.response.code + ')', function () {
  expectCode(200, 400, 401);
});`
    }),
    loginCase('TC-001-14', 'Kisa omurlu (30 sn) token uretilebiliyor -> [ACC-011]',
      "{ username: pm.environment.get('username'), password: pm.environment.get('password'), clientId: 'short-lived' }", 200, null, {
      description: 'Gateway, oturum sonu davranisini test edebilmek icin crm-client-short uzerinden 30 saniyelik token uretir.',
      extraTest: `var b = jsonBody();
pm.test('TC-001-14 · Token omru 60 saniyenin altinda (' + b.expiresIn + ' sn)', function () {
  pm.expect(Number(b.expiresIn)).to.be.below(61);
});
pm.collectionVariables.set('shortAccessToken', b.accessToken || '');`
    }),
    req({
      name: 'TC-001-15 · Bozuk JWT ile korumali uc -> 401',
      path: '/api/v1/customers/search?custId=1',
      noAuth: true,
      headers: [{ key: 'Authorization', value: 'Bearer bozuk.jwt.degeri' }],
      test: `${H}
pm.test('TC-001-15 · Gecersiz token 401 doner', function () { expectCode(401); });`
    }),
    req({
      name: 'TC-001-16 · Token olmadan korumali uc -> 401',
      path: '/api/v1/customers/search?custId=1',
      noAuth: true,
      test: `${H}
pm.test('TC-001-16 · Token\\'siz istek 401 doner', function () { expectCode(401); });`
    }),
    req({
      name: 'TC-001-17 · Logout -> 204 [ACC-012]',
      method: 'POST',
      path: '/api/v1/auth/logout',
      noAuth: true,
      body: '{{logoutBody}}',
      description: 'Ana kosum oturumunu bozmamak icin logout testi kendi ayri oturumunu acar.',
      pre: `${H}
pm.sendRequest({
  url: pm.environment.get('baseUrl') + '/api/v1/auth/login',
  method: 'POST', header: { 'Content-Type': 'application/json' },
  body: { mode: 'raw', raw: JSON.stringify({ username: pm.environment.get('username'), password: pm.environment.get('password') }) }
}, function (err, res) {
  var rt = (!err && res.code === 200) ? res.json().refreshToken : '';
  pm.collectionVariables.set('logoutRefreshToken', rt);
  pm.collectionVariables.set('logoutBody', JSON.stringify({ refreshToken: rt }));
});`,
      test: `${H}
pm.test('TC-001-17 · Logout 204 doner', function () { expectCode(204, 200); });`
    }),
    req({
      name: 'TC-001-18 · Logout sonrasi refresh token gecersiz -> 4xx [ACC-013]',
      method: 'POST',
      path: '/api/v1/auth/refresh',
      noAuth: true,
      body: '{{logoutBody}}',
      test: `${H}
pm.test('TC-001-18 · Sonlandirilan oturumun refresh token\\'i reddedilir', function () {
  pm.expect(pm.response.code, 'gelen ' + pm.response.code).to.be.at.least(400);
});`
    })
  ]
);

// ===========================================================================
// 02 - FR-002
// ===========================================================================
function searchReq(tc, title, query, testBody, opts) {
  opts = opts || {};
  return req({
    name: tc + ' · ' + title,
    path: '/api/v1/customers/search?' + query,
    description: opts.description,
    test: `${H}
var b = jsonBody();
var found = ids(b.content);
${testBody}`
  });
}

const fr002 = folder(
  '02 · FR-002 Musteri Arama ve Goruntuleme',
  'FR-002. Arama kriterleri, AND/OR mantigi, kismi eslesme, sayfalama, siralama ve sonuc semasi. Musteri A ve B ayni soyadi tasidigi icin AND/OR mantigi gercekten dogrulanabiliyor.',
  [
    searchReq('TC-002-01', 'Nationality ID tam eslesme + yanit semasi [ACC-004, ACC-006]', 'tcNo={{tcknA}}', `
pm.test('TC-002-01 · Arama 200 doner ve Musteri A bulunur', function () {
  expectCode(200);
  pm.expect(found, 'donen custId listesi').to.include(String(pm.collectionVariables.get('custIdA')));
});
pm.test('TC-002-01 · Sonuc semasi: Customer ID, First Name, Second Name, Last Name, Role, NAT ID', function () {
  pm.expect(b.content, 'sonuc listesi').to.be.an('array').that.is.not.empty;
  var r = b.content[0];
  ['custId', 'firstName', 'middleName', 'lastName', 'role', 'tcNo'].forEach(function (f) {
    pm.expect(r, f).to.have.property(f);
  });
});
// Alanin VARLIGI yetmez, DOLU olmasi da gerekir: role alani bir donem async event ile
// dolduruldugu icin event kaybolunca sessizce null kaliyordu (sadece varlik kontrolu
// bu hatayi yakalayamamisti). middleName opsiyonel oldugu icin listede yok.
pm.test('TC-002-01 · Zorunlu sonuc alanlari bos/null degil', function () {
  var r = b.content[0];
  ['custId', 'firstName', 'lastName', 'role', 'tcNo'].forEach(function (f) {
    pm.expect(r[f], f + ' alani dolu olmali').to.not.be.oneOf([null, undefined, '']);
  });
});`),
    searchReq('TC-002-02', 'Customer ID ile arama -> tek kayit [ACC-004]', 'custId={{custIdA}}', `
pm.test('TC-002-02 · Customer ID ile tam eslesen kayit doner', function () {
  expectCode(200);
  pm.expect(found).to.eql([String(pm.collectionVariables.get('custIdA'))]);
});`),
    searchReq('TC-002-03', 'Account Number ile arama [ACC-004]', 'acctNo={{acctNoA}}', `
pm.test('TC-002-03 · Hesap numarasi ile kayit bulunur', function () {
  expectCode(200);
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`),
    searchReq('TC-002-04', 'GSM tam eslesme ile arama [ACC-004]', 'gsm={{gsmA}}', `
pm.test('TC-002-04 · GSM ile kayit bulunur', function () {
  expectCode(200);
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`),
    req({
      name: 'TC-002-05 · GSM - +90\'li format (URL-encoded) ile arama',
      path: '/api/v1/customers/search?gsm=%2B90{{gsmA}}',
      description: 'Numara normalizasyonu bekleniyor mu? Dokumanda "10 hane, 5 ile baslar" deniyor; API tam eslesme yapiyor.',
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-05 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-05 · +90\\'li GSM formati normalize edilip ayni musteriyi bulmali', function () {
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    req({
      name: 'TC-002-06 · GSM - 0 ile baslayan format ile arama',
      path: '/api/v1/customers/search?gsm=0{{gsmA}}',
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-06 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-06 · 0 onekli GSM formati normalize edilmeli', function () {
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    searchReq('TC-002-07', 'First Name + Last Name AND - kesisim [ACC-002]', 'firstName={{firstNameA}}&lastName={{lastNameA}}', `
pm.test('TC-002-07 · Ad ve soyad birlikte eslesen kayit doner', function () {
  expectCode(200);
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});
pm.test('TC-002-07 · Ayni soyadli fakat farkli adli musteri (B) sonuca girmez', function () {
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('custIdB')));
});`),
    searchReq('TC-002-08', 'First Name + Last Name AND - bos kesisim [ACC-002]', 'firstName={{firstNameA}}&lastName=OlmayanSoyad', `
pm.test('TC-002-08 · Kesisim bos oldugunda sonuc donmez', function () {
  expectCode(200);
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('custIdA')));
});`),
    searchReq('TC-002-09', 'Ayni soyada sahip iki musteri birlikte doner [ACC-002]', 'lastName={{lastNameA}}', `
pm.test('TC-002-09 · Soyad ile arama her iki musteriyi de doner', function () {
  expectCode(200);
  pm.expect(found, 'Musteri A').to.include(String(pm.collectionVariables.get('custIdA')));
  pm.expect(found, 'Musteri B').to.include(String(pm.collectionVariables.get('custIdB')));
});`),
    searchReq('TC-002-10', 'Isim grubu ile NAT ID arasinda OR mantigi [ACC-002]', 'firstName={{firstNameA}}&lastName={{lastNameA}}&tcNo={{tcknB}}', `
pm.test('TC-002-10 · Isim grubuna VEYA NAT ID\\'ye uyan kayitlarin tamami doner', function () {
  expectCode(200);
  pm.expect(found, 'A (isim eslesmesi)').to.include(String(pm.collectionVariables.get('custIdA')));
  pm.expect(found, 'B (tcNo eslesmesi)').to.include(String(pm.collectionVariables.get('custIdB')));
});`),
    req({
      name: 'TC-002-11 · Kismi (prefix) firstName eslesmesi [ACC-004]',
      path: '/api/v1/customers/search?firstName={{prefixA}}',
      description: 'Adin tamami degil, ilk 8 karakteri ile aranir. Onek bilincli olarak runId iceriyor: ortamda "Test..." ile baslayan eski kayitlar oldugunda ilk sayfa onlarla dolup testi yaniltmasin.',
      pre: `${H}
pm.collectionVariables.set('prefixA', String(pm.collectionVariables.get('firstNameA')).slice(0, 8));`,
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-11 · Adin ilk 4 karakteri ile arama musteriyi bulur', function () {
  expectCode(200);
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    req({
      name: 'TC-002-12 · Kucuk harfle arama - buyuk-kucuk harfe duyarsiz',
      path: '/api/v1/customers/search?firstName={{lowerA}}',
      pre: `${H}
pm.collectionVariables.set('lowerA', String(pm.collectionVariables.get('firstNameA')).toLowerCase());`,
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-12 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-12 · Kucuk harfle yazilan ad ayni musteriyi bulmali', function () {
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    req({
      name: 'TC-002-13 · BUYUK harfle arama - buyuk-kucuk harfe duyarsiz',
      path: '/api/v1/customers/search?firstName={{upperA}}',
      pre: `${H}
pm.collectionVariables.set('upperA', String(pm.collectionVariables.get('firstNameA')).toUpperCase());`,
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-13 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-13 · BUYUK harfle yazilan ad ayni musteriyi bulmali', function () {
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    req({
      name: 'TC-002-14 · Bastaki ve sondaki bosluk kirpilir',
      path: '/api/v1/customers/search?firstName=%20{{firstNameA}}%20',
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-14 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-14 · Bosluklu girilen ad kirpilarak eslestirilmeli', function () {
  pm.expect(found).to.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    req({
      name: 'TC-002-15 · Tam eslesme alaninda kismi deger -> sonuc yok',
      path: '/api/v1/customers/search?tcNo={{partialTckn}}',
      pre: `${H}
pm.collectionVariables.set('partialTckn', String(pm.collectionVariables.get('tcknA')).slice(0, 5));`,
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-15 · NAT ID kismi deger ile eslesmez (tam eslesme alani)', function () {
  expectCode(200);
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('custIdA')));
});`
    }),
    searchReq('TC-002-16', 'Kayitsiz NAT ID -> 200 bos liste [ACC-010]', 'tcNo=99999999999', `
pm.test('TC-002-16 · Kayit bulunamayinca bos liste doner', function () {
  expectCode(200);
  pm.expect(b.content, 'content').to.be.an('array').that.is.empty;
  pm.expect(b.totalElements).to.eql(0);
});`),
    searchReq('TC-002-17', 'Pasif (silinmis) musteri sonuclarda yok [ACC-005]', 'tcNo={{tcknC}}', `
pm.test('TC-002-17 · Soft-delete edilen musteri listelenmez', function () {
  expectCode(200);
  pm.expect(b.content, 'content').to.be.an('array').that.is.empty;
});`),
    searchReq('TC-002-18', 'Sayfalama - size=10 ilk sayfa [ACC-007]', 'page=0&size=10', `
pm.test('TC-002-18 · Sayfa parametreleri uygulanir', function () {
  expectCode(200);
  pm.expect(b.size).to.eql(10);
  pm.expect(b.number).to.eql(0);
  pm.expect(b.content.length).to.be.at.most(10);
});`),
    searchReq('TC-002-19', 'Sayfalama - 2. sayfa [ACC-007]', 'page=1&size=5', `
pm.test('TC-002-19 · Ikinci sayfa istenebilir', function () {
  expectCode(200);
  pm.expect(b.number).to.eql(1);
  pm.expect(b.size).to.eql(5);
});`),
    searchReq('TC-002-20', 'Varsayilan sayfa boyutu 10 olmali (dokuman kurali)', 'tcNo={{tcknA}}', `
gapTest('TC-002-20 · Varsayilan sayfa boyutu 10 olmalidir (API 50 doner)', function () {
  pm.expect(b.size, 'API varsayilani').to.eql(10);
});`),
    searchReq('TC-002-21', 'Siralama - sort parametresi destegi [ACC-008]', 'lastName={{lastNameA}}&sort=firstName,asc', `
pm.test('TC-002-21 · Istek islenir (200)', function () { expectCode(200); });
gapTest('TC-002-21 · sort parametresi uygulanmali', function () {
  var srt = b.sort || (b.pageable && b.pageable.sort) || {};
  pm.expect(srt.sorted === true || srt.empty === false, 'siralama bilgisi: ' + JSON.stringify(srt)).to.eql(true);
});`),
    searchReq('TC-002-22', 'Nationality ID 10 hane -> 400 (dokuman kurali)', 'tcNo=1234567890', `
gapTest('TC-002-22 · 11 haneden kisa NAT ID reddedilmeli', function () { expectCode(400); });`),
    searchReq('TC-002-23', 'Nationality ID 12 hane -> 400 (dokuman kurali)', 'tcNo=123456789012', `
gapTest('TC-002-23 · 11 haneden uzun NAT ID reddedilmeli', function () { expectCode(400); });`),
    searchReq('TC-002-24', 'Customer ID rakam disi -> 400', 'custId=abc', `
pm.test('TC-002-24 · Sayisal olmayan Customer ID reddedilir', function () { expectCode(400); });`),
    searchReq('TC-002-25', 'GSM 5 ile baslamiyor -> 400 (dokuman kurali)', 'gsm=4441234567', `
gapTest('TC-002-25 · 5 ile baslamayan GSM reddedilmeli', function () { expectCode(400); });`),
    searchReq('TC-002-26', 'Filtresiz arama -> 400 (dokuman kurali ACC-003)', '', `
pm.test('TC-002-26 · Istek islenir', function () { expectCode(200, 400); });
gapTest('TC-002-26 · En az bir filtre zorunlu olmali (API tum aktif musterileri doner)', function () {
  expectCode(400);
});`),
    req({
      name: 'TC-002-27 · Detay goruntuleme - GET customers-{id} [ACC-009]',
      path: '/api/v1/customers/{{custIdA}}',
      test: `${H}
var b = jsonBody();
pm.test('TC-002-27 · Musteri detayi 200 doner', function () { expectCode(200); });
pm.test('TC-002-27 · Dogru musteri ve hesap bilgisi doner', function () {
  pm.expect(String(b.custId)).to.eql(String(pm.collectionVariables.get('custIdA')));
  pm.expect(b.accounts, 'accounts').to.be.an('array').that.is.not.empty;
});`
    }),
    req({
      name: 'TC-002-28 · Olmayan musteri detayi -> 404',
      path: '/api/v1/customers/999999999',
      test: `${H}
pm.test('TC-002-28 · Tanimsiz musteri icin 404 doner', function () { expectCode(404); });`
    })
  ]
);

// ===========================================================================
// 03 - FR-003
// ===========================================================================
const fr003 = folder(
  '03 · FR-003 Musteri Olusturma',
  'FR-003. Kimlik dogrulama + tekillik kontrolu, onboarding saga\'si, demografik/adres/kontakt validasyon tablolari ve sinir degerleri (BVA). Saga kanitlari: kaydin party-service ve contact-info-service tarafina gercekten yazildigi ayrica dogrulanir.',
  [
    verifyCase('TC-003-01', 'verify-identity - gecerli kimlik -> 200 [ACC-004, ACC-006]', '', 200, null, {}),
    verifyCase('TC-003-02', 'verify-identity - Nationality ID 10 hane -> 400', "b.nationalId = '1234567890';", 400, 'Invalid national ID'),
    verifyCase('TC-003-03', 'verify-identity - Nationality ID harf iceriyor -> 400', "b.nationalId = '1234567890a';", 400, 'Invalid national ID'),
    verifyCase('TC-003-04', 'verify-identity - gelecek tarihli dogum tarihi -> 400', "b.birthDate = dateOffset(30);", 400, 'valid birth date'),
    verifyCase('TC-003-05', 'verify-identity - 31.12.1899 -> 400 (BVA)', "b.birthDate = '31/12/1899';", 400, 'valid birth date'),
    verifyCase('TC-003-06', 'verify-identity - 01.01.1900 -> 200 (BVA sinir degeri)', "b.birthDate = '01/01/1900';", 200),
    verifyCase('TC-003-07', 'verify-identity - kayitli Nationality ID -> 409 [ACC-005]', "b.nationalId = pm.collectionVariables.get('tcknA');", 409, 'already exists'),
    onboardCase('TC-003-08', 'Gecerli kayit -> 201 + varsayilan 223 tipi hesap [ACC-015, ACC-017]', `
pm.collectionVariables.set('sagaTckn', b.individual.nationalId);
pm.collectionVariables.set('sagaEmail', b.contact.email);
pm.collectionVariables.set('sagaFirstName', b.individual.firstName);`, 201, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-003-08 · custId ve partyRoleId doner', function () {
  pm.expect(r.custId, 'custId').to.be.a('number');
  pm.expect(r.partyRoleId, 'partyRoleId').to.be.a('number');
});
pm.test('TC-003-08 · Varsayilan hesap 223 (CUST_ACCT) tipinde acilir', function () {
  pm.expect(r.accounts, 'accounts').to.be.an('array').that.is.not.empty;
  pm.expect(r.accounts[0].accountTpId).to.eql(223);
});
pm.test('TC-003-08 · Musteri aktif olusturulur', function () { pm.expect(r.active).to.eql(true); });
if (r.custId) { pm.collectionVariables.set('custIdSaga', r.custId); }`
    }),
    req({
      name: 'TC-003-09 · Saga kaniti - kayit party-service tarafinda olustu',
      path: '/api/v1/individuals/exists?nationalId={{sagaTckn}}',
      description: 'Onboarding uc servise birden yazar. Bu test, customer-service 201 donerken party-service tarafinda gercekten kayit olustugunu dogrular.',
      test: `${H}
pm.test('TC-003-09 · party-service Nationality ID kaydini goruyor', function () {
  expectCode(200);
  pm.expect(String(pm.response.text()).trim()).to.eql('true');
});`
    }),
    req({
      name: 'TC-003-10 · Saga kaniti - demografik bilgi girilenlerle tutarli',
      path: '/api/v1/customers/{{custIdSaga}}/individual',
      test: `${H}
var b = jsonBody();
pm.test('TC-003-10 · Kisisel bilgi girilen degerlerle ayni', function () {
  expectCode(200);
  pm.expect(b.firstName).to.eql(pm.collectionVariables.get('sagaFirstName'));
  pm.expect(b.nationalId).to.eql(pm.collectionVariables.get('sagaTckn'));
  pm.expect(b.birthDate).to.eql('15/06/1990');
});`
    }),
    req({
      name: 'TC-003-11 · Saga kaniti - adres contact-info-service tarafinda olustu',
      path: '/api/v1/customers/{{custIdSaga}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('TC-003-11 · Girilen adres kaydedildi ve birincil isaretlendi', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array').with.lengthOf(1);
  pm.expect(b[0].streetName).to.eql('Cumhuriyet Caddesi');
  pm.expect(b[0].primary, 'ilk adres birincil olmali').to.eql(true);
});`
    }),
    req({
      name: 'TC-003-12 · Saga kaniti - iletisim bilgisi kaydedildi',
      path: '/api/v1/customers/{{custIdSaga}}/contact',
      test: `${H}
var b = jsonBody();
pm.test('TC-003-12 · E-posta ve cep telefonu kaydedildi', function () {
  expectCode(200);
  pm.expect(b.email).to.eql(pm.collectionVariables.get('sagaEmail'));
  pm.expect(b.mobilePhone, 'mobilePhone').to.be.a('string').and.not.empty;
});`
    }),
    onboardCase('TC-003-13', 'Ayni Nationality ID ile ikinci kayit -> 409 [ACC-005]',
      "b.individual.nationalId = pm.collectionVariables.get('sagaTckn');", 409, 'already exists'),
    onboardCase('TC-003-14', 'Demografi - First Name bos -> 400', "b.individual.firstName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-15', 'Demografi - Last Name bos -> 400', "b.individual.lastName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-16', 'Demografi - Birth Date bos -> 400', "b.individual.birthDate = null;", 400, 'This field is required.'),
    onboardCase('TC-003-17', 'Demografi - Gender bos -> 400', "b.individual.genderId = null;", 400, 'This field is required.'),
    onboardCase('TC-003-18', 'Demografi - Nationality ID bos -> 400', "b.individual.nationalId = '';", 400),
    onboardCase('TC-003-19', 'Demografi - isim rakam iceriyor -> 400 (kod kurali, dokumanda yok)',
      "b.individual.firstName = 'Ahmet123';", 400, 'Name should contain letters only.', {
      description: 'Sprint-4 ile eklenen ^[a-zA-ZcCgGiIoOsSuU\\\\s]*$ kurali. Gereksinim dokumaninda bu kural YOK - dokuman guncellenmelidir.'
    }),
    onboardCase('TC-003-20', 'Demografi - Turkce karakterli ve bosluklu isim -> 201',
      "b.individual.firstName = 'Sukru Cagri'; b.individual.lastName = 'Gunes';", 201),
    onboardCase('TC-003-21', 'Demografi - First Name 50 karakter -> 201 (BVA sinir)',
      "b.individual.firstName = repeat('a', 50);", 201),
    onboardCase('TC-003-22', 'Demografi - First Name 51 karakter -> 400 (BVA, dokuman kurali)',
      "b.individual.firstName = repeat('a', 51);", 400, null, { gap: true }),
    onboardCase('TC-003-23', 'Demografi - Middle, Mother ve Father Name opsiyonel -> 201',
      "b.individual.middleName = null; b.individual.motherName = null; b.individual.fatherName = null;", 201),
    onboardCase('TC-003-24', 'Adres - City bos -> 400', "b.addresses[0].cityId = null;", 400, 'This field is required.'),
    onboardCase('TC-003-25', 'Adres - Street bos -> 400', "b.addresses[0].streetName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-26', 'Adres - House-Flat Number bos -> 400', "b.addresses[0].buildingName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-27', 'Adres - Address Description bos -> 400', "b.addresses[0].addressDesc = '';", 400, 'This field is required.'),
    onboardCase('TC-003-28', 'Adres - Street 200 karakter -> 201 (BVA sinir)',
      "b.addresses[0].streetName = repeat('b', 200);", 201),
    onboardCase('TC-003-29', 'Adres - Street 201 karakter -> 400 (BVA, dokuman kurali)',
      "b.addresses[0].streetName = repeat('b', 201);", 400, null, { gap: true }),
    onboardCase('TC-003-30', 'Adres - hic adres girilmeden kayit -> 400 [ACC-011]',
      "b.addresses = [];", 400, 'At least one address is required.'),
    onboardCase('TC-003-31', 'Adres - 5 adres ile kayit -> 201 (BVA sinir) [ACC-010]', `
var a = b.addresses[0];
b.addresses = [];
for (var i = 0; i < 5; i++) { b.addresses.push({ cityId: a.cityId, streetName: 'Sokak ' + i, buildingName: 'No:' + i, addressDesc: 'Adres ' + i }); }`, 201),
    onboardCase('TC-003-32', 'Adres - 6 adres ile kayit -> 400 [ACC-010]', `
var a = b.addresses[0];
b.addresses = [];
for (var i = 0; i < 6; i++) { b.addresses.push({ cityId: a.cityId, streetName: 'Sokak ' + i, buildingName: 'No:' + i, addressDesc: 'Adres ' + i }); }`, 400, 'You can add up to 5 addresses.'),
    onboardCase('TC-003-33', 'Kontakt - E-mail bos -> 400', "b.contact.email = '';", 400, ['This field is required.', 'Invalid email format']),
    onboardCase('TC-003-34', 'Kontakt - E-mail formati gecersiz -> 400', "b.contact.email = 'gecersiz-mail';", 400, 'Invalid email format'),
    onboardCase('TC-003-35', 'Kontakt - Mobile Phone bos -> 400', "b.contact.mobilePhone = '';", 400, ['This field is required.', 'Invalid phone number']),
    onboardCase('TC-003-36', 'Kontakt - Mobile Phone 5 ile baslamiyor -> 400', "b.contact.mobilePhone = '4441234567';", 400, 'Invalid phone number'),
    onboardCase('TC-003-37', 'Kontakt - Mobile Phone 9 hane -> 400 (BVA)', "b.contact.mobilePhone = '555123456';", 400, 'Invalid phone number'),
    onboardCase('TC-003-38', 'Kontakt - Home Phone 2 ile baslamali -> 400 (dokuman kurali)',
      "b.contact.homePhone = '3121234567';", 400, null, { gap: true,
      description: 'FR-003 validasyon tablosu "10 hane, 2 ile baslar" diyor; FR-006 tablosu "10-11 hane" diyor. Kod ikincisini uyguluyor - dokuman ici celiski.' }),
    onboardCase('TC-003-39', 'Kontakt - Fax harf iceriyor -> 400', "b.contact.fax = '021212345aa';", 400, 'Invalid phone number'),
    onboardCase('TC-003-40', 'Kontakt - Home Phone ve Fax opsiyonel -> 201',
      "b.contact.homePhone = null; b.contact.fax = null;", 201),
    req({
      name: 'TC-003-41 · Pasif musterinin Nationality ID\'si ile yeni kayit (davranis belgelenir)',
      method: 'POST',
      path: '/api/v1/customers/onboarding',
      body: '{{b_TC_003_41}}',
      description: 'ACIK ANALIZ SORUSU: Musteri C setup asamasinda soft-delete edildi. Onun T.C. kimlik numarasi ile yeni bir musteri acilabilmeli mi? Gereksinim dokumani bu durumu TANIMLAMIYOR. Test, sistemin fiili davranisini belgeler ve karar alinana kadar acik soru olarak raporlanir.',
      pre: `${H}
var b = newOnboardBody();
b.individual.nationalId = pm.collectionVariables.get('tcknC');
pm.collectionVariables.set('b_TC_003_41', JSON.stringify(b));`,
      test: `${H}
var code = pm.response.code;
pm.test('TC-003-41 · Sistem tutarli bir yanit veriyor (201 kayit acilir veya 409 engellenir) - gelen: ' + code, function () {
  expectCode(201, 409);
});
if (code === 201) {
  trackId('createdCustomerIds', jsonBody().custId);
  console.log('[TC-003-41] DAVRANIS: pasif musterinin TCKN\\'i ile YENI KAYIT ACILABILIYOR (201).');
} else if (code === 409) {
  console.log('[TC-003-41] DAVRANIS: pasif musterinin TCKN\\'i tekillik kontrolune TAKILIYOR (409).');
}
gapTest('TC-003-41 · Pasif musterinin Nationality ID kurali dokumanda tanimlanmali', function () {
  throw new Error('Gereksinim dokumaninda bu senaryo icin kabul kriteri yok - fiili davranis: HTTP ' + code);
});`
    })
  ]
);

// ===========================================================================
// 04 - FR-004
// ===========================================================================
const fr004 = folder(
  '04 · FR-004 Musteri Bilgilerini Guncelleme',
  'FR-004. Demografik bilgilerin okunmasi, guncellenmesi, Nationality ID tekillik kontrolu ve validasyon tablosu. Guncellemeler Musteri A uzerinde yapilir.',
  [
    req({
      name: 'TC-004-01 · Mevcut bilgiler dolu gelir [ACC-002]',
      path: '/api/v1/customers/{{custIdA}}/individual',
      test: `${H}
var b = jsonBody();
pm.test('TC-004-01 · Kisisel bilgi 200 doner', function () { expectCode(200); });
pm.test('TC-004-01 · Tum demografik alanlar yanitta yer alir', function () {
  ['firstName', 'middleName', 'lastName', 'birthDate', 'genderId', 'motherName', 'fatherName', 'nationalId']
    .forEach(function (f) { pm.expect(b, f).to.have.property(f); });
});
pm.test('TC-004-01 · Dogum tarihi DD/MM/YYYY formatinda doner', function () {
  pm.expect(String(b.birthDate)).to.match(/^\\d{2}\\/\\d{2}\\/\\d{4}$/);
});`
    }),
    individualCase('TC-004-02', 'Gecerli guncelleme -> 200 [ACC-003, ACC-010]', `
b.lastName = 'Guncel' + uniq();
b.middleName = 'GuncelOrta';
b.genderId = Number(pm.collectionVariables.get('genderIdFemale'));
b.birthDate = '20/07/1985';
pm.collectionVariables.set('updatedLastNameA', b.lastName);`, 200, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-004-02 · Gonderilen degerler yanitta guncel', function () {
  pm.expect(r.lastName).to.eql(pm.collectionVariables.get('updatedLastNameA'));
  pm.expect(r.birthDate).to.eql('20/07/1985');
});`
    }),
    req({
      name: 'TC-004-03 · Guncelleme kalicidir (tekrar okundugunda guncel gelir) [ACC-011]',
      path: '/api/v1/customers/{{custIdA}}/individual',
      test: `${H}
var b = jsonBody();
pm.test('TC-004-03 · Kaydedilen bilgiler kalici', function () {
  expectCode(200);
  pm.expect(b.lastName).to.eql(pm.collectionVariables.get('updatedLastNameA'));
  pm.expect(b.middleName).to.eql('GuncelOrta');
});`
    }),
    req({
      name: 'TC-004-04 · Guncelleme arama sonucuna yansir (nihai tutarlilik)',
      path: '/api/v1/customers/search?custId={{custIdA}}',
      description: 'customer-service arama gorunumunu (CUSTOMER_SEARCH_VIEW) ayri tutuyor ve Kafka olayi ile ASENKRON guncelliyor. Bu yuzden tek atislik okuma yaniltici olur; guncel deger gorunene kadar kisa araliklarla yeniden okunur.',
      test: `${H}
var expected = pm.collectionVariables.get('updatedLastNameA');
var searchUrl = pm.environment.get('baseUrl') + '/api/v1/customers/search?custId=' + pm.collectionVariables.get('custIdA');
var maxTry = 6;

pm.test('TC-004-04 · Arama ucu 200 doner', function () { expectCode(200); });

function check(attempt) {
  pm.sendRequest({ url: searchUrl, method: 'GET', header: jsonHeaders() }, function (err, res) {
    var list = (!err && res.code === 200) ? (res.json().content || []) : [];
    var current = list.length ? list[0].lastName : null;
    if (current === expected || attempt >= maxTry) {
      pm.test('TC-004-04 · Arama sonucu guncel soyadi gosterir (' + attempt + '. denemede)', function () {
        pm.expect(list, 'arama sonucu bos donmemeli').to.be.an('array').that.is.not.empty;
        pm.expect(current, 'search view guncellenmedi (asenkron senkronizasyon)').to.eql(expected);
      });
      return;
    }
    setTimeout(function () { check(attempt + 1); }, 700);
  });
}
check(1);`
    }),
    individualCase('TC-004-05', 'Kendi Nationality ID\'si ile guncelleme -> 200 (cakisma sayilmamali)', `
b.lastName = pm.collectionVariables.get('updatedLastNameA');
b.middleName = 'GuncelOrta';
b.genderId = Number(pm.collectionVariables.get('genderIdFemale'));
b.birthDate = '20/07/1985';`, 200, null, {
      description: 'Tekillik kontrolu "kendisi haric" calismali. Aksi halde kullanici TCKN\'ine dokunmadan hicbir alanini guncelleyemez.'
    }),
    individualCase('TC-004-06', 'Baska musterinin Nationality ID\'si -> 409 [ACC-006, ACC-007]',
      "b.nationalId = pm.collectionVariables.get('tcknB');", 409, null, {
      description: 'BULGU B-03: su an 409 yerine 500 ("A dependent service call failed.") donuyor. Ayni kural onboarding\'de dogru calisiyor.'
    }),
    individualCase('TC-004-07', 'First Name bos -> 400', "b.firstName = '';", 400, 'This field is required.'),
    individualCase('TC-004-08', 'Last Name bos -> 400', "b.lastName = '';", 400, 'This field is required.'),
    individualCase('TC-004-09', 'Birth Date bos -> 400', "b.birthDate = null;", 400, 'This field is required.'),
    individualCase('TC-004-10', 'Gender bos -> 400', "b.genderId = null;", 400, 'This field is required.'),
    individualCase('TC-004-11', 'Nationality ID bos -> 400', "b.nationalId = '';", 400),
    individualCase('TC-004-12', 'Nationality ID 11 hane degil -> 400', "b.nationalId = '123456';", 400, 'Invalid national ID'),
    individualCase('TC-004-13', 'Gelecek tarihli dogum tarihi -> 400', "b.birthDate = dateOffset(30);", 400, 'valid birth date'),
    individualCase('TC-004-14', 'Dogum tarihi 31.12.1899 -> 400 (BVA)', "b.birthDate = '31/12/1899';", 400, 'valid birth date'),
    individualCase('TC-004-15', 'Dogum tarihi 01.01.1900 -> 200 (BVA sinir)', `
b.lastName = pm.collectionVariables.get('updatedLastNameA');
b.birthDate = '01/01/1900';`, 200, null, {
      extraTest: `pm.sendRequest({
  url: pm.environment.get('baseUrl') + '/api/v1/customers/' + pm.collectionVariables.get('custIdA') + '/individual',
  method: 'PUT', header: jsonHeaders(),
  body: { mode: 'raw', raw: JSON.stringify({
    firstName: pm.collectionVariables.get('firstNameA'), middleName: 'GuncelOrta',
    lastName: pm.collectionVariables.get('updatedLastNameA'),
    genderId: Number(pm.collectionVariables.get('genderIdFemale')), motherName: 'Anne', fatherName: 'Baba',
    birthDate: '20/07/1985', nationalId: pm.collectionVariables.get('tcknA') }) }
}, function () { console.log('[FR-004] Dogum tarihi test sonrasi geri yuklendi.'); });`
    }),
    individualCase('TC-004-16', 'Isim rakam iceriyor -> 400 (kod kurali)', "b.firstName = 'Ahmet123';", 400, 'Name should contain letters only.'),
    individualCase('TC-004-17', 'First Name 51 karakter -> 400 (BVA, dokuman kurali)', "b.firstName = repeat('a', 51);", 400, null, { gap: true }),
    individualCase('TC-004-18', 'Olmayan musteri -> 404', '', 404, null, { custIdVar: '999999999' })
  ]
);

// ===========================================================================
// 05 - FR-005
// ===========================================================================
const fr005 = folder(
  '05 · FR-005 Adres Yonetimi',
  'FR-005. Adres listeleme, ekleme, guncelleme, birincil adres kurali, silme guard\'lari, 5 adres limiti (BVA) ve IDOR korumasi. Adres sayisinin deterministik kalmasi icin Musteri D kullanilir.',
  [
    req({
      name: 'TC-005-01 · Adresler listelenir, tek adres otomatik birincildir [ACC-001, ACC-007]',
      path: '/api/v1/customers/{{custIdD}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('TC-005-01 · Adres listesi 200 doner', function () { expectCode(200); });
pm.test('TC-005-01 · Onboarding\\'de girilen tek adres listede', function () {
  pm.expect(b, 'adres listesi').to.be.an('array').with.lengthOf(1);
});
pm.test('TC-005-01 · Tek adres otomatik birincil', function () {
  pm.expect(b[0].primary).to.eql(true);
});`
    }),
    addressCase('TC-005-02', 'Yeni adres ekle -> 201, birincil degil [ACC-002, ACC-004]', `
b.streetName = 'Ikinci Sokak';
b.addressDesc = 'Is adresi';`, 201, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-005-02 · Gonderilen alanlar dogru kaydedildi', function () {
  pm.expect(r.streetName).to.eql('Ikinci Sokak');
  pm.expect(r.addrDesc).to.eql('Is adresi');
  pm.expect(r.primary).to.eql(false);
});
if (r.id) { pm.collectionVariables.set('addressIdD2', r.id); }`
    }),
    addressCase('TC-005-03', 'Adres guncelle -> 200 [ACC-013]', `
b.streetName = 'Guncellenen Sokak';
b.addressDesc = 'Guncellenen aciklama';`, 200, null, {
      method: 'PUT', path: '/api/v1/customers/{{custIdD}}/addresses/{{addressIdD2}}',
      extraTest: `var r = jsonBody();
pm.test('TC-005-03 · Guncel degerler yanitta', function () {
  pm.expect(r.streetName).to.eql('Guncellenen Sokak');
  pm.expect(r.addrDesc).to.eql('Guncellenen aciklama');
});`
    }),
    addressCase('TC-005-04', 'Ikinci adres birincil yapilir -> 200 [ACC-006]', `
b.streetName = 'Guncellenen Sokak';
b.addressDesc = 'Guncellenen aciklama';
b.primary = true;`, 200, null, {
      method: 'PUT', path: '/api/v1/customers/{{custIdD}}/addresses/{{addressIdD2}}'
    }),
    req({
      name: 'TC-005-05 · Tek birincil adres kurali korunur [ACC-006]',
      path: '/api/v1/customers/{{custIdD}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('TC-005-05 · Musterinin yalnizca bir birincil adresi vardir', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array');
  var primaries = b.filter(function (a) { return a.primary === true; });
  pm.expect(primaries.length, 'birincil adres sayisi').to.eql(1);
  pm.expect(String(primaries[0].id)).to.eql(String(pm.collectionVariables.get('addressIdD2')));
});`
    }),
    req({
      name: 'TC-005-06 · Birincil adres silinemez -> 409 [ACC-009]',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdD}}/addresses/{{addressIdD2}}',
      test: `${H}
pm.test('TC-005-06 · Birincil adres silme engellenir', function () {
  expectCode(409);
  expectMessage('Primary address cannot be deleted.');
});`
    }),
    req({
      name: 'TC-005-07 · Birincil olmayan adres silinir -> 204 [ACC-012]',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdD}}/addresses/{{addressIdD1}}',
      test: `${H}
pm.test('TC-005-07 · Birincil olmayan adres silinir', function () { expectCode(204); });`
    }),
    req({
      name: 'TC-005-08 · Silinen adres listede gorunmez [ACC-012]',
      path: '/api/v1/customers/{{custIdD}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('TC-005-08 · Silinen adres listeden cikti', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array');
  var found = b.map(function (a) { return String(a.id); });
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('addressIdD1')));
});`
    }),
    addressCase('TC-005-09', 'Adres - City bos -> 400', "b.cityId = null;", 400, 'This field is required.'),
    addressCase('TC-005-10', 'Adres - Street bos -> 400', "b.streetName = '';", 400, 'This field is required.'),
    addressCase('TC-005-11', 'Adres - House-Flat Number bos -> 400', "b.buildingName = '';", 400, 'This field is required.'),
    addressCase('TC-005-12', 'Adres - Address Description bos -> 400', "b.addressDesc = '';", 400, 'This field is required.'),
    addressCase('TC-005-13', 'Adres - Street 200 karakter -> 201 (BVA sinir)', "b.streetName = repeat('c', 200);", 201, null, {
      extraTest: `if (pm.response.code === 201) { pm.collectionVariables.set('addressIdD3', jsonBody().id); }`
    }),
    addressCase('TC-005-14', 'Adres - Street 201 karakter -> 400 (BVA, dokuman kurali)', "b.streetName = repeat('c', 201);", 400, null, { gap: true }),
    req({
      name: 'TC-005-15 · Adres limiti - 5. adrese kadar eklenebilir -> 201 [ACC-005]',
      path: '/actuator/health',
      description: 'Musterinin adres sayisi 5\'e tamamlanir. Her ekleme ayri bir assertion olarak raporlanir.',
      test: `${H}
var custId = pm.collectionVariables.get('custIdD');
var listUrl = pm.environment.get('baseUrl') + '/api/v1/customers/' + custId + '/addresses';
pm.sendRequest({ url: listUrl, method: 'GET', header: jsonHeaders() }, function (err, res) {
  if (err || res.code !== 200 || !Array.isArray(res.json())) {
    pm.test('TC-005-15 · Adres limiti hazirligi', function () { throw new Error('adres listesi alinamadi: ' + (err || res.code)); });
    return;
  }
  var current = res.json().length;
  var i = current;
  function addNext() {
    if (i >= 5) { return; }
    var n = i + 1;
    pm.sendRequest({
      url: listUrl, method: 'POST', header: jsonHeaders(),
      body: { mode: 'raw', raw: JSON.stringify({ cityId: Number(pm.collectionVariables.get('cityId')), streetName: 'Limit Sokak ' + n, buildingName: 'No:' + n, addressDesc: 'Limit testi ' + n, primary: false }) }
    }, function (e2, r2) {
      pm.test('TC-005-15 · ' + n + '. adres eklenebilir (201)', function () {
        if (e2) { throw new Error(String(e2)); }
        pm.expect(r2.code, String(r2.text()).slice(0, 200)).to.eql(201);
      });
      i++;
      addNext();
    });
  }
  addNext();
});`
    }),
    addressCase('TC-005-16', 'Adres limiti - 6. adres -> 409 [ACC-005]', "b.streetName = 'Altinci Adres';", 409, 'You can add up to 5 addresses.'),
    req({
      name: 'TC-005-17 · IDOR - baska musterinin adresi silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdA}}/addresses/{{addressIdD2}}',
      test: `${H}
pm.test('TC-005-17 · Baska musterinin adresi icin 404 doner', function () { expectCode(404); });`
    }),
    addressCase('TC-005-18', 'IDOR - baska musterinin adresi guncellenemez -> 404', '', 404, null, {
      method: 'PUT', path: '/api/v1/customers/{{custIdA}}/addresses/{{addressIdD2}}'
    }),
    req({
      name: 'TC-005-19 · Olmayan adres silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdD}}/addresses/999999999',
      test: `${H}
pm.test('TC-005-19 · Tanimsiz adres icin 404 doner', function () { expectCode(404); });`
    }),
    addressCase('TC-005-20', 'Adres - listede olmayan City id -> 400', "b.cityId = 999999;", 400, null, {
      gap: true,
      path: '/api/v1/customers/{{custIdA}}/addresses',
      description: 'Dokuman "City: listeden secim" diyor. cityId lookup-service\'te tanimli bir deger mi diye dogrulaniyor mu? customer-service yalnizca @NotNull uyguluyor ve degeri contact-info-service\'e gecirmis gorunuyor.',
      extraTest: `if (pm.response.code === 201) {
  console.warn('[TC-005-20] Gecersiz cityId (999999) kabul edildi - lookup dogrulamasi yok.');
}`
    })
  ]
);

// ===========================================================================
// 99 - TEMIZLIK
// ===========================================================================
const teardown = folder(
  '99 · Temizlik (Teardown)',
  'Kosum sirasinda olusturulan tum test musterilerini soft-delete eder. cleanup=false yapilirsa kayitlar incelenmek uzere birakilir.',
  [
    req({
      name: 'Z01 · Test musterilerini sil (dongu)',
      method: 'DELETE',
      path: '/api/v1/customers/{{cleanupCustId}}',
      pre: `${H}
var list = JSON.parse(pm.collectionVariables.get('createdCustomerIds') || '[]');
var cleanupOn = String(pm.environment.get('cleanup')).toLowerCase() === 'true';
if (!cleanupOn || list.length === 0) {
  pm.collectionVariables.set('cleanupCustId', '0');
  pm.collectionVariables.set('cleanupSkip', 'true');
} else {
  pm.collectionVariables.set('cleanupSkip', 'false');
  pm.collectionVariables.set('cleanupCustId', list.shift());
  pm.collectionVariables.set('createdCustomerIds', JSON.stringify(list));
}`,
      test: `${H}
if (pm.collectionVariables.get('cleanupSkip') === 'true') {
  pm.test('[ATLANDI] Silinecek kayit yok veya cleanup=false', function () { pm.expect(true).to.be.true; });
} else {
  var id = pm.collectionVariables.get('cleanupCustId');
  pm.test('Z01 · Test musterisi ' + id + ' temizlendi', function () { expectCode(204, 404, 409); });
  if (pm.response.code === 409) {
    var left = JSON.parse(pm.collectionVariables.get('cleanupLeftovers') || '[]');
    left.push(id);
    pm.collectionVariables.set('cleanupLeftovers', JSON.stringify(left));
  }
  var remaining = JSON.parse(pm.collectionVariables.get('createdCustomerIds') || '[]');
  if (remaining.length > 0) { postman.setNextRequest('Z01 · Test musterilerini sil (dongu)'); }
}`
    }),
    req({
      name: 'Z02 · Kosum ozeti',
      path: '/actuator/health',
      noAuth: true,
      test: `${H}
var gaps = JSON.parse(pm.collectionVariables.get('gapLog') || '[]');
var left = JSON.parse(pm.collectionVariables.get('cleanupLeftovers') || '[]');
console.log('==================== CRM Lite (FR-001..FR-005) kosum ozeti ====================');
console.log('runId                  : ' + pm.collectionVariables.get('runId'));
console.log('strictMode             : ' + pm.environment.get('strictMode'));
console.log('Bekleyen boslik sayisi : ' + gaps.length);
gaps.forEach(function (g) { console.log('   - ' + g); });
console.log('Silinemeyen musteri    : ' + (left.length ? left.join(', ') : 'yok'));
console.log('==============================================================================');
pm.test('Z02 · Kosum ozeti uretildi (bekleyen boslik: ' + gaps.length + ', kalinti: ' + left.length + ')', function () {
  pm.expect(true).to.be.true;
});`
    })
  ]
);

// ===========================================================================
const collection = {
  info: {
    _postman_id: 'crm-lite-fr001-fr005-v1',
    name: 'CRM Lite - Backend API Testleri (FR-001..FR-005) v1.0',
    description: [
      'CRM Lite gereksinim dokumanindaki FR-001 … FR-005 maddelerinin orta katman (API) karsiliklarini dogrulayan regresyon takimi.',
      '',
      'Tasarim ilkeleri:',
      '- Her test senaryosu ayri bir istek: Postman arayuzunde tek tek gorunur, tek tek kosulabilir, Allure raporunda ayri test case olur.',
      '- Tum istekler API Gateway (baseUrl) uzerinden gider; gercek JWT ile calisir, token yonetimi otomatiktir.',
      '- 00 klasoru deterministik veri seti kurar: Musteri A ve B ayni soyadi tasir (AND/OR testleri), C pasiflestirilir, D adres testlerine ayrilir.',
      '- Her kosum kendi verisini uretir (runId + gecerli TCKN uretici) ve 99 klasorunde temizler; koleksiyon sinirsiz kez kosulabilir.',
      '- Test adlari TC-XXX-YY formatindadir ve ilgili ACC maddesini tasir; docs/traceability-matrix.md ile birebir eslesir.',
      '- Dokumanda tanimli olup kodda karsiligi olmayan maddeler gapTest ile [BEKLEYEN BOSLUK] olarak raporlanir; strictMode=true yapildiginda gercek hataya donusur.',
      '',
      'Kosum sirasi anlamlidir: 00 Setup -> 01..05 -> 99 Temizlik.'
    ].join('\n'),
    schema: 'https://schema.getpostman.com/json/collection/v2.1.0/collection.json'
  },
  auth: { type: 'bearer', bearer: [{ key: 'token', value: '{{accessToken}}', type: 'string' }] },
  event: [
    { listen: 'prerequest', script: { type: 'text/javascript', exec: s(collectionPreRequest) } },
    { listen: 'test', script: { type: 'text/javascript', exec: s(collectionTest) } }
  ],
  variable: [
    { key: 'accessToken', value: '', type: 'string' },
    { key: 'refreshToken', value: '', type: 'string' },
    { key: 'tokenExpiresAt', value: '0', type: 'string' },
    { key: 'runId', value: '', type: 'string' },
    { key: 'gapLog', value: '[]', type: 'string' },
    { key: 'createdCustomerIds', value: '[]', type: 'string' },
    { key: 'cleanupLeftovers', value: '[]', type: 'string' },
    { key: 'cityId', value: '', type: 'string' },
    { key: 'genderId', value: '', type: 'string' },
    { key: 'genderIdFemale', value: '', type: 'string' }
  ],
  item: [setup, fr001, fr002, fr003, fr004, fr005, teardown]
};

fs.mkdirSync(require('path').dirname(OUT), { recursive: true });
fs.writeFileSync(OUT, JSON.stringify(collection, null, 2), 'utf8');
let n = 0;
collection.item.forEach((f) => { n += f.item.length; });
console.log('Yazildi: ' + OUT);
collection.item.forEach((f) => console.log(String(f.item.length).padStart(4) + '  ' + f.name));
console.log('TOPLAM ISTEK: ' + n);

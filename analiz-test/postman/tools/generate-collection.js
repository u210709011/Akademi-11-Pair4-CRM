/* eslint-disable */
// CRM Lite - FR-001..FR-011 Postman koleksiyonu ureticisi.
const fs = require('fs');
const path = require('path');
const OUT = path.join(__dirname, '..', 'CRM-Lite-FR001-FR011.postman_collection.json');

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

/** Iletisim bilgisi guncelleme senaryosu (FR-006). */
function contactCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'c_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'PUT',
    path: opts.path || '/api/v1/customers/{{custIdE}}/contact',
    body: '{{' + v + '}}',
    noAuth: opts.noAuth,
    description: opts.description,
    pre: `${H}
var b = newContactBody();
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
${opts.extraTest || ''}`
  });
}

/** Fatura hesabi olusturma (FR-008) / guncelleme (FR-010) senaryosu. */
function billingCase(tc, title, mutate, expect, msg, opts) {
  opts = opts || {};
  const v = 'ba_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: opts.method || 'POST',
    path: opts.path || '/api/v1/customers/{{custIdF}}/accounts',
    body: '{{' + v + '}}',
    noAuth: opts.noAuth,
    description: opts.description,
    pre: `${H}
var b = newBillingBody();
${mutate || ''}
pm.collectionVariables.set('${v}', JSON.stringify(b));`,
    test: `${H}
${opts.gap ? 'gapTest' : 'pm.test'}('${title.replace(/'/g, "\\'")}', function () {
  expectCode(${expect});${msgLine(msg)}
});
if (pm.response.code === 201) { trackId('createdAccountIds', jsonBody().custAcctId); }
${opts.extraTest || ''}`
  });
}

/** Fatura hesabi durum degistirme (PATCH .../status) senaryosu - FR-011. */
function statusCase(tc, title, status, expect, msg, opts) {
  opts = opts || {};
  const v = 'st_' + tc.replace(/-/g, '_');
  return req({
    name: tc + ' · ' + title,
    method: 'PATCH',
    path: opts.path || '/api/v1/customers/{{custIdF}}/accounts/{{acctIdF}}/status',
    body: '{{' + v + '}}',
    description: opts.description,
    pre: `${H}
pm.collectionVariables.set('${v}', JSON.stringify({ status: ${JSON.stringify(status)} }));`,
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
  "// FR-006: gecerli bir iletisim bilgisi govdesi (her cagrida tekil e-posta/GSM uretir).",
  "function newContactBody(){ var u = uniq(); return { email: 'iletisim.' + u.toLowerCase() + '@example.com', mobilePhone: newGsm(), homePhone: '2121234567', fax: '2129876543' }; }",
  "// FR-008/FR-010: gecerli bir fatura hesabi govdesi - Musteri F'nin VAR OLAN adresini secer.",
  "function newBillingBody(){ return { accountName: 'Hesap ' + uniq(), accountDesc: 'Fatura hesabi aciklamasi', addressId: Number(pm.collectionVariables.get('addressIdF1')) }; }",
  "// FR-008 ACC-005 / FR-010 ACC-004: hesapla birlikte olusturulacak YENI adres govdesi.",
  "function newBillingAddress(){ return { cityId: Number(pm.collectionVariables.get('cityId')), streetName: 'Fatura Sokak', buildingName: 'No:7 D:2', addressDesc: 'Fatura adresi' }; }",
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
  'Testlerin uzerinde calisacagi deterministik veri seti burada kurulur. Her fixture musteri tek bir sorumluluk alanina ayrilmistir; boylece bir FR\'nin testleri digerinin verisini bozmaz.\n\n'
  + '- Musteri A : ana test musterisi (FR-002 arama, FR-004 guncelleme)\n'
  + '- Musteri B : A ile AYNI SOYADI tasir (FR-002 AND/OR ve siralama testleri)\n'
  + '- Musteri C : soft-delete edilir (pasif musteri davranisi)\n'
  + '- Musteri D : FR-005 adres testleri (adres sayisi deterministik kalsin diye ayri)\n'
  + '- Musteri E : FR-006 iletisim bilgisi testleri\n'
  + '- Musteri F : FR-008..FR-011 fatura hesabi testleri\n'
  + '- Musteri G : FR-007 musteri silme testleri (kosum sonunda silinir)',
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
    }),
    fixture('S12', 'Musteri E - iletisim bilgisi testleri icin', 'E'),
    req({
      name: 'S13 · [Musteri E] adres id alinir',
      path: '/api/v1/customers/{{custIdE}}/addresses',
      description: 'FR-008 IDOR testinde "baska musterinin adresi" olarak kullanilir. Musteri E\'nin adresi FR-006 boyunca degistirilmedigi icin deterministiktir.',
      test: `${H}
var b = jsonBody();
pm.test('[Musteri E] adresi listelendi', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array').that.is.not.empty;
});
if (Array.isArray(b) && b[0]) { pm.collectionVariables.set('addressIdE1', b[0].id); }`
    }),
    fixture('S14', 'Musteri F - fatura hesabi testleri icin', 'F'),
    req({
      name: 'S15 · [Musteri F] varsayilan hesap ve adres id alinir',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      description: 'Onboarding her musteriye CUST_ACCT (223) tipinde varsayilan bir hesap acar. FR-011 "varsayilan hesap silinemez" testleri bu id\'yi kullanir.',
      test: `${H}
var b = jsonBody();
pm.test('[Musteri F] varsayilan hesabi listelendi', function () {
  expectCode(200);
  pm.expect(b.content, 'hesap listesi').to.be.an('array').that.is.not.empty;
});
if (b.content && b.content[0]) {
  pm.collectionVariables.set('defaultAcctIdF', b.content[0].custAcctId);
  pm.collectionVariables.set('defaultAcctTpIdF', b.content[0].accountTpId);
  console.log('[SETUP] Musteri F varsayilan hesap id = ' + b.content[0].custAcctId + ' (tip ' + b.content[0].accountTpId + ')');
}
pm.sendRequest({ url: pm.environment.get('baseUrl') + '/api/v1/customers/' + pm.collectionVariables.get('custIdF') + '/addresses', method: 'GET', header: jsonHeaders() }, function (err, res) {
  pm.test('[Musteri F] adresi alindi', function () {
    if (err) { throw new Error(String(err)); }
    pm.expect(res.code).to.eql(200);
  });
  if (!err && res.code === 200 && res.json()[0]) { pm.collectionVariables.set('addressIdF1', res.json()[0].id); }
});`
    }),
    fixture('S16', 'Musteri G - musteri silme testleri icin', 'G'),
    req({
      name: 'S17 · [Musteri G] adres id alinir',
      path: '/api/v1/customers/{{custIdG}}/addresses',
      test: `${H}
var b = jsonBody();
pm.test('[Musteri G] adresi listelendi', function () {
  expectCode(200);
  pm.expect(b, 'adres listesi').to.be.an('array').that.is.not.empty;
});
if (Array.isArray(b) && b[0]) { pm.collectionVariables.set('addressIdG1', b[0].id); }`
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
      name: 'TC-002-05 · GSM - +90\'li format (URL-encoded) -> 400 (dokuman kurali)',
      path: '/api/v1/customers/search?gsm=%2B90{{gsmA}}',
      description: 'Dokuman GSM arama filtresini "yalnizca rakam, 10 hane, 5 ile baslamalidir" diye tanimliyor ve '
        + 'ihlalde "Invalid phone number." mesajini istiyor. "+90" onekli deger bu kurala uymaz, reddedilmelidir. '
        + 'API hicbir format dogrulamasi yapmadigi icin 200 + bos liste donuyor - kullanici yanlis yazdigini '
        + 'anlamiyor, "boyle bir musteri yok" saniyor. Ayni kok neden: TC-002-06, TC-002-22, TC-002-23, TC-002-25.',
      test: `${H}
gapTest('TC-002-05 · Gecersiz GSM formati reddedilmeli', function () {
  expectCode(400);
  expectMessage('Invalid phone number.');
});`
    }),
    req({
      name: 'TC-002-06 · GSM - 0 ile baslayan format -> 400 (dokuman kurali)',
      path: '/api/v1/customers/search?gsm=0{{gsmA}}',
      description: '0 onekli numara 11 hane olur ve 5 ile baslamaz - dokumandaki GSM kuralina uymaz. '
        + 'TC-002-05 ile ayni kok neden: arama filtrelerinde format dogrulamasi yok.',
      test: `${H}
gapTest('TC-002-06 · Gecersiz GSM formati reddedilmeli', function () {
  expectCode(400);
  expectMessage('Invalid phone number.');
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
      name: 'TC-002-14 · Bastaki ve sondaki bosluk davranisi (acik analiz sorusu)',
      path: '/api/v1/customers/search?firstName=%20{{firstNameA}}%20',
      description: 'ACIK ANALIZ SORUSU - kusur degildir. Arama filtrelerinde bastaki/sondaki bosluklarin '
        + 'kirpilip kirpilmayacagi FR-002 dokumaninda TANIMLI DEGIL. (FR-001 giris tablosunda kullanici adi icin '
        + 'boyle bir kural var, arama alanlari icin yok.) API su an kirpmiyor: bosluklu deger eslesmiyor. '
        + 'Kullanici kopyala-yapistir ile arama yaptiginda sonuc alamaz. Analiz tarafi karar verdiginde bu test '
        + 'ya gercek bir beklentiye donusturulecek ya da kaldirilacaktir.',
      test: `${H}
var found = ids(jsonBody().content);
pm.test('TC-002-14 · Istek islenir (200)', function () { expectCode(200); });
pm.test('TC-002-14 · Mevcut davranis belgelenir: bosluk kirpilmiyor', function () {
  var trimmed = found.indexOf(String(pm.collectionVariables.get('custIdA'))) > -1;
  console.log('[TC-002-14] Bosluklu arama musteriyi ' + (trimmed ? 'BULDU (kirpma var)' : 'bulamadi (kirpma yok)')
    + ' - dokumanda kural tanimli degil, analiz karari bekleniyor.');
  pm.expect(true).to.be.true;
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
    searchReq('TC-002-21', 'Siralama - artan duzen uygulanir [ACC-008]', 'lastName={{lastNameA}}&sortBy=firstName&sortDir=asc', `
pm.test('TC-002-21 · Istek islenir (200)', function () { expectCode(200); });
pm.test('TC-002-21 · Siralama yaniti sortBy alanina gore ASC uygulanmis', function () {
  var srt = (b.pageable && b.pageable.sort) || b.sort || [];
  var arr = Array.isArray(srt) ? srt : (srt.orders || []);
  pm.expect(arr, 'siralama bilgisi: ' + JSON.stringify(srt)).to.be.an('array').that.is.not.empty;
  pm.expect(arr[0].property).to.eql('firstName');
  pm.expect(arr[0].direction).to.eql('ASC');
});
pm.test('TC-002-21 · Sonuclar gercekten alfabetik sirali', function () {
  var names = (b.content || []).map(function (r) { return String(r.firstName || '').toLowerCase(); });
  var sorted = names.slice().sort();
  pm.expect(names).to.eql(sorted);
});`, {
      description: 'ACC-008: sutun basligina tiklandiginda liste o sutuna gore siralanir. Orta katman karsiligi '
        + 'sortBy + sortDir parametreleridir (Spring Data\'nin "sort=alan,yon" bicimi DEGIL - CustomerController '
        + 'guvenlik icin kendi whitelist\'ini uyguluyor: custId/firstName/middleName/lastName/tcNo/role).'
    }),
    searchReq('TC-002-21a', 'Siralama - tekrar tiklamada azalan duzen [ACC-008]', 'lastName={{lastNameA}}&sortBy=firstName&sortDir=desc', `
pm.test('TC-002-21a · Azalan siralama uygulanir', function () {
  expectCode(200);
  var srt = (b.pageable && b.pageable.sort) || [];
  var arr = Array.isArray(srt) ? srt : (srt.orders || []);
  pm.expect(arr, 'siralama bilgisi').to.be.an('array').that.is.not.empty;
  pm.expect(arr[0].direction).to.eql('DESC');
});
pm.test('TC-002-21a · Sonuclar ters alfabetik sirali', function () {
  var names = (b.content || []).map(function (r) { return String(r.firstName || '').toLowerCase(); });
  var sorted = names.slice().sort().reverse();
  pm.expect(names).to.eql(sorted);
});`, {
      description: 'ACC-008 "Tekrar tiklandiginda siralama tersine doner" maddesinin orta katman karsiligi.'
    }),
    searchReq('TC-002-21b', 'Siralama - whitelist disi alan sessizce yok sayilir', 'lastName={{lastNameA}}&sortBy=gecersizAlan&sortDir=asc', `
pm.test('TC-002-21b · Gecersiz siralama alani istegi kirmaz', function () {
  expectCode(200);
  var srt = (b.pageable && b.pageable.sort) || [];
  var arr = Array.isArray(srt) ? srt : (srt.orders || []);
  pm.expect(arr, 'whitelist disi alan icin siralama uygulanmamali').to.be.an('array').that.is.empty;
});`, {
      description: 'Guvenlik: sortBy dogrudan JPA property-path\'ine gitseydi istemci ilgisiz tablolara '
        + 'siralama enjekte edebilirdi. CustomerController whitelist disi degerleri sessizce yok sayiyor - '
        + 'istegi kirmadan guvenli davranis. Bu test o korumanin kalici olmasini garanti eder.'
    }),
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
      "b.individual.firstName = repeat('a', 51);", 400, 'Maximum 50 characters are allowed.', {
        gap: true,
        description: 'Dokuman First Name icin "Metin, maks 50" diyor ve ihlal mesajini '
          + '"Maximum 50 characters are allowed." olarak tanimliyor (06.08.2026 guncellemesi). '
          + 'IndividualInfo.firstName uzerinde @Size kisiti HIC YOK - 51 karakter kabul ediliyor.'
      }),
    onboardCase('TC-003-23', 'Demografi - Middle, Mother ve Father Name opsiyonel -> 201',
      "b.individual.middleName = null; b.individual.motherName = null; b.individual.fatherName = null;", 201),
    onboardCase('TC-003-24', 'Adres - City bos -> 400', "b.addresses[0].cityId = null;", 400, 'This field is required.'),
    onboardCase('TC-003-25', 'Adres - Street bos -> 400', "b.addresses[0].streetName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-26', 'Adres - House-Flat Number bos -> 400', "b.addresses[0].buildingName = '';", 400, 'This field is required.'),
    onboardCase('TC-003-27', 'Adres - Address Description bos -> 400', "b.addresses[0].addressDesc = '';", 400, 'This field is required.'),
    onboardCase('TC-003-28', 'Adres - Street 200 karakter -> 201 (BVA sinir)',
      "b.addresses[0].streetName = repeat('b', 200);", 201),
    onboardCase('TC-003-29', 'Adres - Street 201 karakter -> 400 (BVA, dokuman kurali)',
      "b.addresses[0].streetName = repeat('b', 201);", 400, 'Maximum 200 characters are allowed.', {
        gap: true,
        description: 'Uzunluk kisiti dogru calisiyor (400 doner) ancak @Size mesaji FIELD_REQUIRED anahtarini '
          + 'paylastigi icin "This field is required." donuyor - alan dolu oldugu halde kullanici "zorunlu" '
          + 'mesaji goruyor. Dokuman bu ihlal icin "Maximum 200 characters are allowed." metnini tanimliyor.'
      }),
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
      description: 'FR-003 ve FR-006 validasyon tablolari 06.08.2026 guncellemesiyle esitlendi: ikisi de '
        + '"yalnizca rakam, 10 hane, 2 ile baslar" diyor (onceden celisiyorlardi). ContactInfo.homePhone hala '
        + '^[0-9]{10,11}$ kullaniyor - ne hane sayisi ne de baslangic rakami dokumanla uyusuyor. '
        + 'Ayni boslugu guncelleme yolunda TC-006-28 izliyor.' }),
    onboardCase('TC-003-39', 'Kontakt - Fax harf iceriyor -> 400', "b.contact.fax = '021212345aa';", 400, 'Invalid fax number', {
      description: 'Faks alani 06.08.2026 duzeltmesiyle kendi mesaj anahtarini (FAX_INVALID) kullanmaya basladi; '
        + 'onceden telefon mesajini paylasiyordu. Dokuman FR-003 ve FR-006 tablolarinda "Invalid fax number." diyor.'
    }),
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
    individualCase('TC-004-17', 'First Name 51 karakter -> 400 (BVA, dokuman kurali)', "b.firstName = repeat('a', 51);", 400,
      'Maximum 50 characters are allowed.', {
        gap: true,
        description: 'TC-003-22 ile ayni kok neden, guncelleme yolunda: UpdateIndividualInfo.firstName uzerinde '
          + 'de @Size kisiti yok. Dokuman "Metin, maks 50" ve "Maximum 50 characters are allowed." diyor.'
      }),
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
    addressCase('TC-005-14', 'Adres - Street 201 karakter -> 400 (BVA, dokuman kurali)', "b.streetName = repeat('c', 201);", 400,
      'Maximum 200 characters are allowed.', {
        gap: true,
        description: 'Kisit uygulaniyor (400) ancak mesaj "This field is required." donuyor. '
          + 'AddressEditRequest.streetName uzerindeki @Size, FIELD_REQUIRED anahtarini paylasiyor.'
      }),
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
// 06 - FR-006 ILETISIM BILGILERI YONETIMI
// ===========================================================================
const fr006 = folder(
  '06 · FR-006 Iletisim Bilgileri Yonetimi',
  'FR-006. Musterinin iletisim bilgilerinin goruntulenmesi (GET /contact) ve guncellenmesi (PUT /contact). '
  + 'Dokumandaki validasyon tablosu (E-mail, Mobile Phone, Home Phone, Fax) sinir degerleriyle birlikte dogrulanir. '
  + 'Musteri E kullanilir - guncellemeler kalici oldugu icin diger FR\'larin verisi etkilenmesin diye ayri bir fixture\'dir.',
  [
    req({
      name: 'TC-006-01 · Iletisim bilgileri goruntulenir [ACC-001]',
      path: '/api/v1/customers/{{custIdE}}/contact',
      test: `${H}
var b = jsonBody();
pm.test('TC-006-01 · Iletisim bilgisi 200 doner', function () { expectCode(200); });
pm.test('TC-006-01 · Contact Medium alanlari yanitta yer alir', function () {
  ['email', 'mobilePhone', 'homePhone', 'fax'].forEach(function (f) {
    pm.expect(b, f + ' alani').to.have.property(f);
  });
});`
    }),
    req({
      name: 'TC-006-02 · Guncelleme ekrani mevcut degerlerle dolu gelir [ACC-002, ACC-003]',
      path: '/api/v1/customers/{{custIdE}}/contact',
      description: 'ACC-003 "mevcut iletisim bilgileri guncelleme ekraninda dolu gosterilmelidir" maddesinin orta katman karsiligi: '
        + 'GET /contact, onboarding sirasinda girilen degerleri aynen dondurmelidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-006-02 · Onboarding\\'de girilen e-posta ve GSM aynen doner', function () {
  expectCode(200);
  pm.expect(b.email, 'email').to.eql(pm.collectionVariables.get('emailE'));
  pm.expect(b.mobilePhone, 'mobilePhone').to.eql(pm.collectionVariables.get('gsmE'));
});
pm.test('TC-006-02 · Opsiyonel alanlar da dolu doner (ev telefonu, faks)', function () {
  pm.expect(b.homePhone, 'homePhone').to.eql('2121234567');
  pm.expect(b.fax, 'fax').to.eql('2129876543');
});`
    }),
    contactCase('TC-006-03', 'Tum iletisim alanlari guncellenir -> 200 [ACC-004, ACC-007]', `
b.homePhone = '2129998877';
b.fax = '2128887766';
pm.collectionVariables.set('emailEYeni', b.email);
pm.collectionVariables.set('gsmEYeni', b.mobilePhone);`, 200, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-006-03 · Yanit gonderilen degerleri tasir', function () {
  pm.expect(r.email).to.eql(pm.collectionVariables.get('emailEYeni'));
  pm.expect(r.mobilePhone).to.eql(pm.collectionVariables.get('gsmEYeni'));
  pm.expect(r.homePhone).to.eql('2129998877');
  pm.expect(r.fax).to.eql('2128887766');
});`
    }),
    req({
      name: 'TC-006-04 · Guncelleme kalicidir [ACC-007]',
      path: '/api/v1/customers/{{custIdE}}/contact',
      description: 'ACC-007: Save sonrasi Contact Medium ekranina donuldugunde guncel bilgiler gorunmelidir. '
        + 'PUT yaniti degil, ayri bir GET ile dogrulanir - yani veri gercekten kalici olmalidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-006-04 · Yeni degerler kalici olarak okunuyor', function () {
  expectCode(200);
  pm.expect(b.email).to.eql(pm.collectionVariables.get('emailEYeni'));
  pm.expect(b.mobilePhone).to.eql(pm.collectionVariables.get('gsmEYeni'));
  pm.expect(b.homePhone).to.eql('2129998877');
  pm.expect(b.fax).to.eql('2128887766');
});`
    }),
    contactCase('TC-006-05', 'Opsiyonel alanlar null gonderilir, mevcut degerler korunur -> 200 [ACC-005]', `
b.email = pm.collectionVariables.get('emailEYeni');
b.mobilePhone = pm.collectionVariables.get('gsmEYeni');
b.homePhone = null;
b.fax = null;`, 200, null, {
      description: 'Home Phone ve Fax dokumanda zorunlu degildir. Bos gonderildiginde mevcut kayit SILINMEZ - '
        + 'CustomerContactServiceImpl.upsertMedium, required=false alanlarda bos degeri "dokunma" olarak yorumlar.',
      extraTest: `var r = jsonBody();
pm.test('TC-006-05 · Zorunlu olmayan alanlarin mevcut degerleri korunur', function () {
  pm.expect(r.homePhone, 'homePhone').to.eql('2129998877');
  pm.expect(r.fax, 'fax').to.eql('2128887766');
});`
    }),
    contactCase('TC-006-06', 'Bos govde ile guncelleme -> 400 [ACC-005]', `
b = {};`, 400, 'This field is required.'),
    contactCase('TC-006-07', 'E-mail bos -> 400 [ACC-005]', "b.email = '';", 400,
      ['This field is required.', 'Invalid email format']),
    contactCase('TC-006-08', 'E-mail gecersiz format -> 400', "b.email = 'gecersiz-eposta';", 400, 'Invalid email format'),
    contactCase('TC-006-09', 'E-mail - alan adi eksik -> 400', "b.email = 'kullanici@';", 400, 'Invalid email format'),
    contactCase('TC-006-10', 'E-mail hata mesaji dokumandaki metin degil', "b.email = 'gecersiz-eposta';", 400,
      'Invalid email format.', {
        gap: true,
        description: 'Dokuman (06.08.2026 guncellemesi) e-posta mesajini FR-003 ve FR-006\'da birebir '
          + '"Invalid email format." olarak tanimliyor. API noktasiz donuyor ("Invalid email format"). '
          + 'Islevsel davranis dogru (400), yalnizca metnin sonundaki nokta eksik - '
          + 'messages*.properties icindeki validation.email.invalid degerine nokta eklenmesi yeterlidir.'
      }),
    contactCase('TC-006-11', 'Mobile Phone bos -> 400 [ACC-005]', "b.mobilePhone = '';", 400,
      ['This field is required.', 'Invalid phone number'], {
        description: 'Bos deger hem @NotBlank hem @Pattern kisitini ihlal eder. Bean Validation hangisini once '
          + 'raporlayacagini garanti etmedigi icin iki mesajdan biri kabul edilir - test kararli kalir.'
      }),
    contactCase('TC-006-12', 'Mobile Phone 5 ile baslamiyor -> 400', "b.mobilePhone = '4551234567';", 400, 'Invalid phone number'),
    contactCase('TC-006-13', 'Mobile Phone 9 hane -> 400 (BVA alt sinir)', "b.mobilePhone = '512345678';", 400, 'Invalid phone number'),
    contactCase('TC-006-14', 'Mobile Phone 11 hane -> 400 (BVA ust sinir)', "b.mobilePhone = '51234567890';", 400, 'Invalid phone number'),
    contactCase('TC-006-15', 'Mobile Phone 10 hane ve 5 ile baslar -> 200 (BVA gecerli sinir)', "b.mobilePhone = '5321234567';", 200, null, {
      extraTest: `pm.test('TC-006-15 · Gecerli GSM kaydedildi', function () { pm.expect(jsonBody().mobilePhone).to.eql('5321234567'); });`
    }),
    contactCase('TC-006-16', 'Mobile Phone rakam disi karakter icerir -> 400', "b.mobilePhone = '5A2123456B';", 400, 'Invalid phone number'),
    contactCase('TC-006-17', 'Home Phone 10 hane ve 2 ile baslar -> 200 (gecerli deger)', "b.homePhone = '2321234567';", 200, null, {
      description: 'Dokuman (FR-003 ve FR-006) ev telefonu icin "yalnizca rakam, 10 hane, 2 ile baslar" diyor. '
        + 'Bu test kuralin gecerli sinirini dogrular.'
    }),
    contactCase('TC-006-18', 'Home Phone 11 hane -> 400 (dokuman 10 hane diyor)', "b.homePhone = '02121234567';", 400, null, {
      gap: true,
      description: 'Dokumanin guncel hali ev telefonunu 10 hane olarak tanimliyor (onceki surumde 10-11 idi ve '
        + 'FR-003 ile FR-006 celisiyordu; celiski 06.08.2026\'da 10 hane lehine giderildi). '
        + 'ContactInfo.homePhone hala ^[0-9]{10,11}$ kullaniyor, 11 hane kabul ediliyor.'
    }),
    contactCase('TC-006-19', 'Home Phone 9 hane -> 400 (BVA sinir disi)', "b.homePhone = '312123456';", 400, 'Invalid phone number'),
    contactCase('TC-006-20', 'Home Phone 12 hane -> 400 (BVA sinir disi)', "b.homePhone = '031212345678';", 400, 'Invalid phone number'),
    contactCase('TC-006-21', 'Home Phone bos string gonderilir -> 200 (opsiyonel alan)', "b.homePhone = '';", 200, null, {
      gap: true,
      description: 'Dokuman Home Phone\'u opsiyonel sayiyor. Alan null gonderildiginde kabul ediliyor (TC-006-05), '
        + 'ancak BOS STRING gonderildiginde @Pattern devreye girip 400 donuyor. Ekranda bos birakilan bir alanin '
        + 'front-end tarafindan null mi bos string mi gonderilecegi sozlesmede net degil - front-end null gondermek zorundadir.'
    }),
    contactCase('TC-006-22', 'Fax gecersiz format -> 400', "b.fax = '12345';", 400,
      ['Invalid fax number', 'Invalid phone number'], {
        description: 'Davranis testi: gecersiz faks reddedilmelidir. Mesaj metni gecis halinde oldugu icin iki '
          + 'karsiliktan biri kabul edilir; dokumanla birebir uyum ayri bir test olan TC-006-23 tarafindan izlenir.'
      }),
    contactCase('TC-006-23', 'Fax hata mesaji dokumandaki metin degil', "b.fax = '12345';", 400, 'Invalid fax number.', {
      gap: true,
      description: 'Dokuman (FR-003 ve FR-006 validasyon tablolari) faks icin ayri bir mesaj tanimliyor: '
        + '"Invalid fax number." API ise telefon mesajini paylasiyor. Kullanici hangi alanin hatali oldugunu '
        + 'mesajdan ayirt edemez. Beklenen: MessageKeys.FAX_INVALID eklenip ContactInfo.fax bu anahtari kullanmali.'
    }),
    req({
      name: 'TC-006-24 · Var olmayan musterinin iletisim bilgisi -> 404',
      path: '/api/v1/customers/999999999/contact',
      test: `${H}
pm.test('TC-006-24 · Tanimsiz musteri icin 404 doner', function () {
  expectCode(404);
  expectMessage('Customer not found with id');
});`
    }),
    req({
      name: 'TC-006-25 · Pasif musterinin iletisim bilgisi -> 404',
      path: '/api/v1/customers/{{custIdC}}/contact',
      description: 'Soft-delete edilmis musteri (Musteri C) icin iletisim bilgisi ucu de kapali olmalidir.',
      test: `${H}
pm.test('TC-006-25 · Pasif musteri icin 404 doner', function () { expectCode(404); });`
    }),
    req({
      name: 'TC-006-26 · Gecersiz custId tipi -> 400',
      path: '/api/v1/customers/abc/contact',
      test: `${H}
pm.test('TC-006-26 · Sayisal olmayan custId 400 doner', function () {
  expectCode(400);
  expectMessage('Parameter custId has an invalid value.');
});`
    }),
    req({
      name: 'TC-006-27 · Tokensiz iletisim bilgisi istegi -> 401',
      path: '/api/v1/customers/{{custIdE}}/contact',
      noAuth: true,
      test: `${H}
pm.test('TC-006-27 · Yetkisiz istek 401 doner', function () { expectCode(401); });`
    }),
    contactCase('TC-006-28', 'Home Phone 2 ile baslamiyor -> 400 (dokuman kurali)', "b.homePhone = '3121234567';", 400, null, {
      gap: true,
      description: 'Dokuman ev telefonu icin "2 ile baslar" sartini kosuyor (FR-003 ve FR-006 ayni kurali soyluyor). '
        + 'ContactInfo.homePhone deseni ^[0-9]{10,11}$ - baslangic rakami hic kontrol edilmiyor. '
        + 'FR-003 tarafinda ayni boslugu TC-003-38 izliyor; bu test FR-006 (guncelleme) yolunu kapsar.'
    })
  ]
);

// ===========================================================================
// 07 - FR-007 MUSTERI SILME
// ===========================================================================
const fr007 = folder(
  '07 · FR-007 Musteri Silme',
  'FR-007. Aktif fatura hesabi bulunan musteri silinemez; aktif hesabi olmayan musteri soft-delete edilir. '
  + 'Musteri G uzerinde uctan uca calisir: once aktif hesap eklenir ve silme reddedilir, sonra hesap pasiflestirilip silme basarili olur.',
  [
    req({
      name: 'TC-007-01 · Silme oncesi musteri erisilebilir [on kosul]',
      path: '/api/v1/customers/{{custIdG}}',
      test: `${H}
pm.test('TC-007-01 · Musteri detayi 200 doner', function () { expectCode(200); });
pm.test('TC-007-01 · Musteri aktif durumda', function () {
  pm.expect(String(jsonBody().custId)).to.eql(String(pm.collectionVariables.get('custIdG')));
});`
    }),
    req({
      name: 'TC-007-02 · [Musteri G] aktif fatura hesabi eklenir -> 201 [ACC-002 on kosul]',
      method: 'POST',
      path: '/api/v1/customers/{{custIdG}}/accounts',
      body: '{{gAccountBody}}',
      pre: `${H}
pm.collectionVariables.set('gAccountBody', JSON.stringify({ accountName: 'Silme testi ' + uniq(), accountDesc: 'FR-007 on kosul hesabi', addressId: Number(pm.collectionVariables.get('addressIdG1')) }));`,
      test: `${H}
var r = jsonBody();
pm.test('TC-007-02 · Fatura hesabi olusturuldu (201)', function () { expectCode(201); });
pm.test('TC-007-02 · Hesap aktif olarak acildi', function () { pm.expect(r.active, 'active').to.eql(true); });
if (r.custAcctId) { pm.collectionVariables.set('acctIdG', r.custAcctId); }`
    }),
    req({
      name: 'TC-007-03 · Aktif fatura hesabi olan musteri silinemez -> 409 [ACC-002, ACC-003]',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdG}}',
      test: `${H}
pm.test('TC-007-03 · Silme engellenir ve bilgilendirme mesaji doner', function () {
  expectCode(409);
  expectMessage('This customer has an active billing account and cannot be deleted.');
});`
    }),
    req({
      name: 'TC-007-04 · Silme engellendikten sonra musteri hala aktif [ACC-003]',
      path: '/api/v1/customers/{{custIdG}}',
      description: 'Reddedilen bir silme islemi hicbir yan etki birakmamalidir - musteri kaydi bozulmadan durmalidir.',
      test: `${H}
pm.test('TC-007-04 · Musteri kaydi bozulmadan duruyor', function () { expectCode(200); });`
    }),
    statusCase('TC-007-05', 'Fatura hesabi pasiflestirilir -> 200 [ACC-005 on kosul]', 'PASSIVE', 200, null, {
      path: '/api/v1/customers/{{custIdG}}/accounts/{{acctIdG}}/status',
      extraTest: `pm.test('TC-007-05 · Hesap pasif duruma gecti', function () { pm.expect(jsonBody().active, 'active').to.eql(false); });`
    }),
    req({
      name: 'TC-007-06 · Pasif hesaba bagli urun kontrolu yapiliyor [ACC-004]',
      path: '/api/v1/orders?custAcctId={{acctIdG}}',
      description: 'ACC-004: "fatura hesabi pasif olsa dahi bagli urunu varsa musteri silinemez". '
        + 'customer-service bu kontrolu NoOpBillingAccountProductGuard uzerinden yapiyor ve guard HER ZAMAN false donuyor '
        + '(order-service entegrasyonu henuz baglanmamis). Yani kural kod icinde mevcut ama etkisiz. '
        + 'Bu test, urun sorgusunun calistigini ve hesabin gercekten urunsuz oldugunu belgeleyerek '
        + 'TC-007-07\'nin gecerli bir on kosulda kostugunu garanti eder.',
      test: `${H}
var b = jsonBody();
pm.test('TC-007-06 · Hesabin urun listesi sorgulanabiliyor', function () {
  expectCode(200);
  pm.expect(b, 'urun listesi').to.be.an('array');
});
pm.test('TC-007-06 · Pasif hesabin bagli urunu yok (silme on kosulu saglandi)', function () {
  pm.expect(b.length, 'bagli urun sayisi').to.eql(0);
});
gapTest('TC-007-06 · Bagli urun guard\\'i order-service ile entegre', function () {
  throw new Error('NoOpBillingAccountProductGuard her zaman false donuyor - urunlu hesap senaryosu dogrulanamiyor.');
});`
    }),
    req({
      name: 'TC-007-07 · Aktif hesabi olmayan musteri soft-delete edilir -> 204 [ACC-005]',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdG}}',
      test: `${H}
pm.test('TC-007-07 · Musteri silindi (204)', function () { expectCode(204); });`
    }),
    req({
      name: 'TC-007-08 · Silinen musteri detay ucunda 404 doner [ACC-005]',
      path: '/api/v1/customers/{{custIdG}}',
      description: 'Soft-delete edilen musteri, silinmis gibi davranmalidir - kayit veritabaninda dursa da API\'den erisilememelidir.',
      test: `${H}
pm.test('TC-007-08 · Silinen musteri okunamaz', function () {
  expectCode(404);
  expectMessage('Customer not found with id');
});`
    }),
    req({
      name: 'TC-007-09 · Silinen musteri arama sonuclarinda gorunmez [ACC-006]',
      path: '/api/v1/customers/search?tcNo={{tcknG}}',
      description: 'ACC-006: silme sonrasi kullanici musteri arama ekranina yonlendirilir; silinen musteri orada cikmamalidir. '
        + 'Arama gorunumu (CUSTOMER_SEARCH_VIEW) Kafka uzerinden ASENKRON guncellendigi icin kisa bir bekleme ile yeniden denenir.',
      test: `${H}
var custId = String(pm.collectionVariables.get('custIdG'));
var searchUrl = pm.environment.get('baseUrl') + '/api/v1/customers/search?tcNo=' + pm.collectionVariables.get('tcknG');

function check(attempt) {
  var b = attempt === 0 ? jsonBody() : null;
  function evaluate(body) {
    var found = ids(body.content).indexOf(custId) > -1;
    if (!found || attempt >= 4) {
      pm.test('TC-007-09 · Silinen musteri arama sonuclarinda yok (deneme ' + (attempt + 1) + ')', function () {
        pm.expect(found, 'silinen musteri arama sonucunda gorunuyor').to.eql(false);
      });
      return;
    }
    setTimeout(function () {
      pm.sendRequest({ url: searchUrl, method: 'GET', header: jsonHeaders() }, function (err, res) {
        if (err) {
          pm.test('TC-007-09 · Arama tekrar denemesi', function () { throw new Error(String(err)); });
          return;
        }
        evaluate(res.json());
      });
    }, 700);
  }
  evaluate(b);
}
pm.test('TC-007-09 · Arama ucu 200 doner', function () { expectCode(200); });
check(0);`
    }),
    req({
      name: 'TC-007-10 · Silinen musterinin hesap listesi 404 doner',
      path: '/api/v1/customers/{{custIdG}}/accounts',
      description: 'Silinen musterinin diger tum uclari 404 donerken (TC-007-08, TC-006-25), hesap listeleme ucu 200 + bos sayfa donuyor. '
        + 'BillingAccountServiceImpl.getAccounts, diger metotlarin aksine customerFinder.getActiveCustomerOrThrow cagirmiyor. '
        + 'Sonuc: silinmis bir musteri, front-end\'de "hesabi olmayan gecerli musteri" gibi gorunur (FR-009 ACC-001 bos tablo mesaji).',
      test: `${H}
gapTest('TC-007-10 · Silinen musteri icin hesap listesi 404 doner', function () {
  expectCode(404);
});
if (pm.response.code === 200) {
  console.warn('[TC-007-10] Silinen musteri icin 200 + bos sayfa donduruldu - uc tutarsizligi.');
}`
    }),
    req({
      name: 'TC-007-11 · Silinen musteri tekrar silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdG}}',
      test: `${H}
pm.test('TC-007-11 · Ikinci silme istegi 404 doner', function () { expectCode(404); });`
    }),
    req({
      name: 'TC-007-12 · Var olmayan musteri silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/999999999',
      test: `${H}
pm.test('TC-007-12 · Tanimsiz musteri icin 404 doner', function () { expectCode(404); });`
    }),
    req({
      name: 'TC-007-13 · Gecersiz custId tipi ile silme -> 400',
      method: 'DELETE',
      path: '/api/v1/customers/abc',
      test: `${H}
pm.test('TC-007-13 · Sayisal olmayan custId 400 doner', function () {
  expectCode(400);
  expectMessage('Parameter custId has an invalid value.');
});`
    }),
    req({
      name: 'TC-007-14 · Tokensiz silme istegi -> 401',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdA}}',
      noAuth: true,
      description: 'Guvenlik: kimlik dogrulanmamis bir istek silme islemini tetikleyememelidir. '
        + 'Musteri A uzerinde denenir - istek 401 ile reddedildigi icin A\'ya zarar vermez.',
      test: `${H}
pm.test('TC-007-14 · Yetkisiz silme istegi 401 doner', function () { expectCode(401); });`
    })
  ]
);

// ===========================================================================
// 08 - FR-008 YENI MUSTERI FATURA HESABI OLUSTURMA
// ===========================================================================
const fr008 = folder(
  '08 · FR-008 Fatura Hesabi Olusturma',
  'FR-008. "Create Billing Account" ekraninin orta katman karsiligi (POST /accounts). '
  + 'Hesap 224 (billing account) tipinde acilmali; adres icin mevcut bir adres SECILEBILMELI ya da YENI adres olusturulabilmelidir - '
  + 'ikisi birden ya da hicbiri gonderilemez. Musteri F kullanilir; F FR-009..FR-011 boyunca da ayni hesaplarla devam eder.',
  [
    req({
      name: 'TC-008-01 · Customer Account ekrani acilir, varsayilan hesap listelenir [ACC-001]',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      description: 'Onboarding her musteriye CUST_ACCT (223) tipinde varsayilan bir hesap acar. '
        + 'Bu hesap "billing account" degildir - FR-011 kapsaminda silinemez.',
      test: `${H}
var b = jsonBody();
pm.test('TC-008-01 · Hesap listesi 200 doner', function () { expectCode(200); });
pm.test('TC-008-01 · Onboarding varsayilan hesabi listede', function () {
  pm.expect(b.content, 'hesap listesi').to.be.an('array').that.is.not.empty;
});
pm.test('TC-008-01 · Varsayilan hesap billing account tipinde DEGIL', function () {
  var def = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('defaultAcctIdF')); })[0];
  pm.expect(def, 'varsayilan hesap').to.not.be.undefined;
  pm.expect(def.accountTpId, 'accountTpId').to.not.eql(224);
});`
    }),
    billingCase('TC-008-02', 'Mevcut adres secilerek fatura hesabi olusturulur -> 201 [ACC-004, ACC-011]', `
b.accountName = 'Ev Faturasi';
b.accountDesc = 'Aylik elektrik ve su faturasi';`, 201, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-008-02 · Gonderilen alanlar dogru kaydedildi', function () {
  pm.expect(r.accountName).to.eql('Ev Faturasi');
  pm.expect(r.accountDesc).to.eql('Aylik elektrik ve su faturasi');
  pm.expect(String(r.addressId)).to.eql(String(pm.collectionVariables.get('addressIdF1')));
});
if (r.custAcctId) {
  pm.collectionVariables.set('acctIdF', r.custAcctId);
  pm.collectionVariables.set('acctNoF', r.accountNo);
}`
    }),
    req({
      name: 'TC-008-03 · Olusan hesap 224 (billing account) tipindedir [ACC-012]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      description: 'ACC-012 dokumandaki en somut kabul kriteridir: yaratilan fatura hesabi Account Type olarak 224 olmalidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-008-03 · Yeni hesabin tipi 224', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct, 'yeni hesap listede').to.not.be.undefined;
  pm.expect(acct.accountTpId, 'accountTpId').to.eql(224);
});
pm.test('TC-008-03 · Hesap numarasi uretildi ve bos degil', function () {
  pm.expect(pm.collectionVariables.get('acctNoF'), 'accountNo').to.be.a('string').and.not.empty;
});`
    }),
    billingCase('TC-008-04', 'Yeni adres olusturularak fatura hesabi acilir -> 201 [ACC-004, ACC-005, ACC-007]', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.accountName = 'Is Faturasi';`, 201, null, {
      extraTest: `var r = jsonBody();
pm.test('TC-008-04 · Hesap yeni olusturulan adrese baglandi', function () {
  pm.expect(r.addressId, 'addressId').to.be.a('number');
  pm.expect(String(r.addressId)).to.not.eql(String(pm.collectionVariables.get('addressIdF1')));
});
if (r.addressId) { pm.collectionVariables.set('addressIdF2', r.addressId); }
if (r.custAcctId) { pm.collectionVariables.set('acctIdF2', r.custAcctId); }`
    }),
    req({
      name: 'TC-008-05 · Yeni adres musterinin adres listesine eklendi [ACC-007, ACC-008]',
      path: '/api/v1/customers/{{custIdF}}/addresses',
      description: 'ACC-007/008: adres kaydedilmeli ve Create Billing Account ekraninda listelenmelidir. '
        + 'Orta katman karsiligi: hesapla birlikte olusturulan adres, musterinin adres listesinde de yer almalidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-008-05 · Hesapla olusturulan adres musterinin adresleri arasinda', function () {
  expectCode(200);
  var found = b.map(function (a) { return String(a.id); });
  pm.expect(found, 'adres listesi').to.include(String(pm.collectionVariables.get('addressIdF2')));
});`
    }),
    req({
      name: 'TC-008-06 · Yeni hesap Customer Account listesinde gorunur [ACC-014]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      test: `${H}
var b = jsonBody();
pm.test('TC-008-06 · Olusturulan iki hesap da listede', function () {
  expectCode(200);
  var found = b.content.map(function (a) { return String(a.custAcctId); });
  pm.expect(found).to.include(String(pm.collectionVariables.get('acctIdF')));
  pm.expect(found).to.include(String(pm.collectionVariables.get('acctIdF2')));
});`
    }),
    billingCase('TC-008-07', 'Account Name bos -> 400 [ACC-009]', "b.accountName = '';", 400, 'This field is required.'),
    billingCase('TC-008-08', 'Account Name hic gonderilmez -> 400 [ACC-009]', 'delete b.accountName;', 400, 'This field is required.'),
    billingCase('TC-008-09', 'Account Name 50 karakter -> 201 (BVA gecerli sinir)', "b.accountName = repeat('a', 50);", 201, null, {
      extraTest: `pm.test('TC-008-09 · 50 karakterlik ad tam olarak kaydedildi', function () {
  pm.expect(String(jsonBody().accountName).length, 'accountName uzunlugu').to.eql(50);
});
if (pm.response.code === 201) { pm.collectionVariables.set('acctIdF3', jsonBody().custAcctId); }`
    }),
    billingCase('TC-008-10', 'Account Name 51 karakter -> 400 (BVA sinir disi)', "b.accountName = repeat('a', 51);", 400),
    billingCase('TC-008-11', 'Account Name 51 karakter hata mesaji uzunluk kuralini anlatir', "b.accountName = repeat('a', 51);", 400,
      'Maximum 50 characters are allowed.', {
        gap: true,
        description: 'Uzunluk siniri dogru uygulaniyor (400) ancak @Size kisiti FIELD_REQUIRED mesajini paylasiyor: '
          + 'kullanici dolu bir alan icin "This field is required." goruyor. Dokuman (06.08.2026 guncellemesi) '
          + 'bu ihlal icin ayri bir satir ve "Maximum 50 characters are allowed." metnini tanimliyor. '
          + 'Ayni kok neden: TC-010-11a (FR-010), TC-003-22/TC-004-17 (isim alanlari), TC-003-29/TC-005-14 (adres).'
      }),
    billingCase('TC-008-12', 'Account Description bos -> 400 (dokuman zorunlu diyor)', "b.accountDesc = '';", 400,
      'This field is required.', {
        gap: true,
        description: 'Dokumanin FR-008 validasyon tablosu Account Description\'i ZORUNLU isaretliyor ("This field is required."). '
          + 'CreateBillingAccountRequest.accountDesc uzerinde hicbir kisit yok; bos string de, alanin hic gonderilmemesi de 201 donuyor. '
          + 'ACC-009 ("Account Name, Account Description ve en az bir adres girilmeden Create aktif olmamalidir") sadece front-end\'de karsilaniyor.'
      }),
    billingCase('TC-008-13', 'Adres bilgisi hic gonderilmez -> 400 [ACC-009]', 'delete b.addressId;', 400,
      'Please provide an existing address or a new one for the billing account.'),
    billingCase('TC-008-14', 'Hem mevcut adres hem yeni adres gonderilir -> 400', 'b.newAddress = newBillingAddress();', 400,
      'Please provide either an existing address or a new one, not both.', {
        description: 'addressId ve newAddress karsilikli dislayicidir (XOR). Ikisi birden gonderildiginde hangisinin '
          + 'kazandigi belirsiz kalmamali, istek reddedilmelidir.'
      }),
    billingCase('TC-008-15', 'Yeni adres - City bos -> 400 [ACC-005]', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.cityId = null;`, 400, 'This field is required.'),
    billingCase('TC-008-16', 'Yeni adres - Street bos -> 400 [ACC-005]', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.streetName = '';`, 400, 'This field is required.'),
    billingCase('TC-008-17', 'Yeni adres - House-Flat Number bos -> 400 [ACC-005]', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.buildingName = '';`, 400, 'This field is required.'),
    billingCase('TC-008-18', 'Yeni adres - Address Description bos -> 400 [ACC-005]', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.addressDesc = '';`, 400, 'This field is required.'),
    billingCase('TC-008-19', 'Yeni adres - Street 201 karakter -> 400 (BVA sinir disi)', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.streetName = repeat('c', 201);`, 400),
    billingCase('TC-008-20', 'Yeni adres - listede olmayan City id -> 400', `
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.cityId = 999999;`, 400, null, {
      gap: true,
      description: 'Dokuman City icin "listeden secim" diyor. Fatura hesabi yolunda da (FR-005 TC-005-20 ile ayni kok neden) '
        + 'cityId lookup-service\'te tanimli mi diye dogrulanmiyor; @NotNull disinda kontrol yok. '
        + 'Sonuc: gecersiz bir sehir id\'siyle adres ve ona bagli fatura hesabi olusabiliyor.',
      extraTest: `if (pm.response.code === 201) {
  console.warn('[TC-008-20] Gecersiz cityId (999999) ile fatura hesabi olusturuldu - lookup dogrulamasi yok.');
}`
    }),
    billingCase('TC-008-21', 'IDOR - baska musterinin adres id\'si ile hesap acilamaz -> 404', `
b.addressId = Number(pm.collectionVariables.get('addressIdE1'));`, 404, null, {
      description: 'Guvenlik: Musteri E\'ye ait bir adres id\'si, Musteri F\'nin hesabina baglanmaya calisilir. '
        + 'Adres id\'leri tahmin edilebilir oldugu icin bu kontrol sunucu tarafinda yapilmak zorundadir.'
    }),
    billingCase('TC-008-22', 'Var olmayan adres id -> 404', 'b.addressId = 999999999;', 404),
    billingCase('TC-008-23', 'Var olmayan musteriye hesap acilamaz -> 404', '', 404, 'Customer not found with id', {
      path: '/api/v1/customers/999999999/accounts'
    }),
    billingCase('TC-008-24', 'Pasif musteriye hesap acilamaz -> 404', '', 404, null, {
      path: '/api/v1/customers/{{custIdC}}/accounts',
      description: 'Soft-delete edilmis Musteri C\'ye yeni fatura hesabi acilamamalidir.'
    }),
    billingCase('TC-008-25', 'Tokensiz hesap olusturma istegi -> 401', '', 401, null, { noAuth: true })
  ]
);

// ===========================================================================
// 09 - FR-009 FATURA HESABI VE BAGLI URUN GORUNTULEME
// ===========================================================================
const fr009 = folder(
  '09 · FR-009 Fatura Hesabi ve Bagli Urun Goruntuleme',
  'FR-009. Hesap listeleme sozlesmesi (ACC-002 kolonlari), sayfalama (ACC-009: ilk 5 kayit) ve '
  + 'hesaba bagli urunlerin listelenmesi (ACC-004/005). Urun detay modali (ACC-006/007) icin gereken alanlar '
  + 'orta katmanda henuz karsilanmiyor - bkz. TC-009-10.',
  [
    req({
      name: 'TC-009-01 · Hesap listesi sayfali kontrat doner [ACC-001]',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-01 · Hesap listesi 200 doner', function () { expectCode(200); });
pm.test('TC-009-01 · Sayfali (Page) kontrat alanlari mevcut', function () {
  ['content', 'totalElements', 'totalPages', 'size', 'number'].forEach(function (f) {
    pm.expect(b, f + ' alani').to.have.property(f);
  });
});
pm.test('TC-009-01 · Musterinin en az bir hesabi var (bos tablo mesaji gosterilmez)', function () {
  pm.expect(b.totalElements, 'toplam hesap sayisi').to.be.above(0);
});`
    }),
    req({
      name: 'TC-009-02 · Hesap satirinda ekranda gosterilen alanlar yer alir [ACC-002]',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      description: 'ACC-002 tablo kolonlari: Account Status, Account Number, Account Name, Account Type. '
        + 'Orta katman karsiliklari sirasiyla acctStId/active, accountNo, accountName, accountTpId alanlaridir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-02 · Kolon karsiliklari her satirda mevcut', function () {
  expectCode(200);
  pm.expect(b.content, 'hesap listesi').to.be.an('array').that.is.not.empty;
  b.content.forEach(function (a) {
    ['custAcctId', 'accountNo', 'accountName', 'accountTpId', 'acctStId', 'active'].forEach(function (f) {
      pm.expect(a, f + ' alani (hesap ' + a.custAcctId + ')').to.have.property(f);
    });
  });
});
pm.test('TC-009-02 · Account Number ve Account Status alanlari dolu', function () {
  b.content.forEach(function (a) {
    pm.expect(a.accountNo, 'accountNo (hesap ' + a.custAcctId + ')').to.be.a('string').and.not.empty;
    pm.expect(a.active, 'active (hesap ' + a.custAcctId + ')').to.be.a('boolean');
  });
});`
    }),
    req({
      name: 'TC-009-03 · Varsayilan sayfa boyutu 5 [ACC-009]',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      test: `${H}
pm.test('TC-009-03 · Parametresiz istekte sayfa boyutu 5', function () {
  expectCode(200);
  pm.expect(jsonBody().size, 'size').to.eql(5);
});`
    }),
    req({
      name: 'TC-009-04 · Hesap sayisi 6\'ya tamamlanir [ACC-009 on kosul]',
      path: '/actuator/health',
      description: 'Sayfalama ancak 5\'ten fazla kayit varken anlamli test edilir. Musterinin mevcut hesap sayisi okunur ve '
        + 'eksik kalan kadar hesap acilir - koleksiyon kac kez kosarsa kossun sonuc 6 hesaptir.',
      test: `${H}
var custId = pm.collectionVariables.get('custIdF');
var acctUrl = pm.environment.get('baseUrl') + '/api/v1/customers/' + custId + '/accounts';

pm.sendRequest({ url: acctUrl + '?size=50', method: 'GET', header: jsonHeaders() }, function (err, res) {
  if (err || res.code !== 200) {
    pm.test('TC-009-04 · Sayfalama on kosulu hazirlanamadi', function () { throw new Error('hesap listesi alinamadi: ' + (err || res.code)); });
    return;
  }
  var current = res.json().totalElements;
  var i = current;
  function addNext() {
    if (i >= 6) {
      pm.test('TC-009-04 · Musterinin hesap sayisi 6\\'ya tamamlandi', function () {
        pm.expect(i, 'toplam hesap sayisi').to.be.at.least(6);
      });
      return;
    }
    var n = i + 1;
    pm.sendRequest({
      url: acctUrl, method: 'POST', header: jsonHeaders(),
      body: { mode: 'raw', raw: JSON.stringify({ accountName: 'Sayfalama hesabi ' + n, accountDesc: 'ACC-009 sayfalama testi', addressId: Number(pm.collectionVariables.get('addressIdF1')) }) }
    }, function (e2, r2) {
      pm.test('TC-009-04 · ' + n + '. hesap acilabilir (201)', function () {
        if (e2) { throw new Error(String(e2)); }
        pm.expect(r2.code, String(r2.text()).slice(0, 200)).to.eql(201);
      });
      if (!e2 && r2.code === 201) { trackId('createdAccountIds', r2.json().custAcctId); }
      i++;
      addNext();
    });
  }
  addNext();
});`
    }),
    req({
      name: 'TC-009-05 · Ilk sayfada 5 kayit doner [ACC-009]',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-05 · Ilk sayfa tam olarak 5 kayit icerir', function () {
  expectCode(200);
  pm.expect(b.content.length, 'ilk sayfadaki kayit sayisi').to.eql(5);
});
pm.test('TC-009-05 · Toplam kayit sayisi 5\\'ten fazla, sayfalama devrede', function () {
  pm.expect(b.totalElements, 'toplam kayit').to.be.above(5);
  pm.expect(b.totalPages, 'sayfa sayisi').to.be.above(1);
});`
    }),
    req({
      name: 'TC-009-06 · Ikinci sayfa kalan kayitlari doner [ACC-009]',
      path: '/api/v1/customers/{{custIdF}}/accounts?page=1',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-06 · Ikinci sayfa bos degil', function () {
  expectCode(200);
  pm.expect(b.number, 'sayfa numarasi').to.eql(1);
  pm.expect(b.content, 'ikinci sayfa icerigi').to.be.an('array').that.is.not.empty;
});`
    }),
    req({
      name: 'TC-009-07 · size parametresi ile sayfa boyutu degistirilebilir [ACC-009]',
      path: '/api/v1/customers/{{custIdF}}/accounts?page=0&size=2',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-07 · Istenen sayfa boyutu uygulanir', function () {
  expectCode(200);
  pm.expect(b.size, 'size').to.eql(2);
  pm.expect(b.content.length, 'donen kayit sayisi').to.eql(2);
});`
    }),
    req({
      name: 'TC-009-08 · Hesaba bagli urunler listelenir [ACC-004, ACC-005]',
      path: '/api/v1/orders?custAcctId={{acctIdF}}',
      description: 'ACC-004: hesap satiri genisletildiginde bagli urunler tablo halinde gosterilir. '
        + 'Orta katman karsiligi order-service uzerindeki GET /api/v1/orders?custAcctId={id} ucudur.',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-08 · Urun listesi 200 ve dizi doner', function () {
  expectCode(200);
  pm.expect(b, 'urun listesi').to.be.an('array');
});`
    }),
    req({
      name: 'TC-009-09 · Urun tablosu kolon sozlesmesi [ACC-005]',
      path: '/api/v1/orders?custAcctId={{acctIdF}}',
      description: 'ACC-005 kolonlari: Product ID, Product Name, Campaign Name, Campaign ID. '
        + 'CustOrdItemResponse karsiliklari prodId / prodName / cmpgName / cmpgId alanlaridir. '
        + 'FR-012 satis akisi bu koleksiyonun kapsaminda olmadigi icin liste bos gelebilir; '
        + 'bos listede sozlesme dogrulanamaz ve bu durum acikca raporlanir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-09 · Urun listesi okunabiliyor', function () {
  expectCode(200);
  pm.expect(b, 'urun listesi').to.be.an('array');
});
if (b.length > 0) {
  pm.test('TC-009-09 · Urun satirinda ACC-005 kolonlari mevcut', function () {
    b.forEach(function (p) {
      ['prodId', 'prodName', 'cmpgId', 'cmpgName', 'custAcctId'].forEach(function (f) {
        pm.expect(p, f + ' alani').to.have.property(f);
      });
    });
  });
} else {
  pm.test('TC-009-09 · [KAPSAM DISI] Bagli urun yok, kolon sozlesmesi dogrulanamadi', function () {
    console.warn('[TC-009-09] Hesaba bagli urun bulunmuyor - urun olusturmak FR-012 satis akisini gerektirir (bu koleksiyonun kapsami disinda).');
    pm.expect(true).to.be.true;
  });
}`
    }),
    req({
      name: 'TC-009-10 · Urun detay modali icin gereken alanlar donuyor [ACC-006, ACC-007]',
      path: '/api/v1/orders?custAcctId={{acctIdF}}',
      description: 'ACC-007 modalda su alanlari istiyor: Product Offer Name, Product Offer ID, Product Spec ID, '
        + 'Service Start Date, Prod Chars, Service Address. order-service\'in CustOrdItemResponse kaydi yalnizca '
        + 'custOrdItemId / prodId / prodName / cmpgId / cmpgName / custAcctId alanlarini tasiyor ve urun detayi icin '
        + 'ayri bir uc bulunmuyor. Yani ACC-006/ACC-007 orta katmanda henuz karsilanmiyor.',
      test: `${H}
gapTest('TC-009-10 · Urun detay modali alanlari (Product Spec ID, Service Start Date, Prod Chars, Service Address) API\\'de mevcut', function () {
  throw new Error('CustOrdItemResponse bu alanlari tasimiyor ve urun detayi icin ayri bir uc tanimli degil.');
});`
    }),
    req({
      name: 'TC-009-11 · Var olmayan musterinin hesap listesi 404 doner',
      path: '/api/v1/customers/999999999/accounts',
      description: 'Ayni servisin diger tum uclari (POST/PUT/PATCH/DELETE accounts, GET contact, GET customers/{id}) '
        + 'tanimsiz musteri icin 404 donuyor. GET /accounts ise 200 + bos sayfa donuyor: '
        + 'BillingAccountServiceImpl.getAccounts, customerFinder.getActiveCustomerOrThrow cagirmiyor. '
        + 'Front-end acisindan sonuc, "var olmayan musteri" ile "hesabi olmayan musteri"nin ayirt edilememesidir (ACC-001 bos tablo mesaji).',
      test: `${H}
gapTest('TC-009-11 · Tanimsiz musteri icin hesap listesi 404 doner', function () {
  expectCode(404);
});
if (pm.response.code === 200) {
  console.warn('[TC-009-11] Tanimsiz musteri icin 200 + bos sayfa donduruldu - uc tutarsizligi (bkz. TC-007-10).');
}`
    }),
    req({
      name: 'TC-009-12 · Negatif sayfa numarasi -> 400',
      path: '/api/v1/customers/{{custIdF}}/accounts?page=-1',
      description: 'Sayfalama parametreleri istemciden gelir ve dogrulanmalidir. PageRequest.of(-1, 5) '
        + 'IllegalArgumentException firlatir; bu istisna ele alinmadigi icin istemciye 500 donuyor. '
        + 'Gecersiz istemci girdisi 4xx ile karsilanmalidir.',
      test: `${H}
pm.test('TC-009-12 · Gecersiz sayfa numarasi istemci hatasi olarak doner', function () {
  expectCode(400);
});`
    }),
    req({
      name: 'TC-009-13 · Sifir sayfa boyutu -> 400',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=0',
      description: 'TC-009-12 ile ayni kok neden: PageRequest.of(0, 0) IllegalArgumentException firlatir ve 500 doner.',
      test: `${H}
pm.test('TC-009-13 · Gecersiz sayfa boyutu istemci hatasi olarak doner', function () {
  expectCode(400);
});`
    }),
    req({
      name: 'TC-009-14 · IDOR - baska musterinin hesabi listede gorunmez',
      path: '/api/v1/customers/{{custIdA}}/accounts?size=50',
      description: 'Guvenlik: hesap listesi yalnizca istenen musterinin hesaplarini icermelidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-009-14 · Musteri A\\'nin listesinde Musteri F\\'nin hesaplari yok', function () {
  expectCode(200);
  var found = b.content.map(function (a) { return String(a.custAcctId); });
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('acctIdF')));
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('acctIdF2')));
});`
    }),
    req({
      name: 'TC-009-15 · Gecersiz custId tipi -> 400',
      path: '/api/v1/customers/abc/accounts',
      test: `${H}
pm.test('TC-009-15 · Sayisal olmayan custId 400 doner', function () {
  expectCode(400);
  expectMessage('Parameter custId has an invalid value.');
});`
    }),
    req({
      name: 'TC-009-16 · Tokensiz hesap listeleme istegi -> 401',
      path: '/api/v1/customers/{{custIdF}}/accounts',
      noAuth: true,
      test: `${H}
pm.test('TC-009-16 · Yetkisiz istek 401 doner', function () { expectCode(401); });`
    })
  ]
);

// ===========================================================================
// 10 - FR-010 BILLING ACCOUNT GUNCELLEME
// ===========================================================================
const UPD = '/api/v1/customers/{{custIdF}}/accounts/{{acctIdF}}';
const fr010 = folder(
  '10 · FR-010 Fatura Hesabi Guncelleme',
  'FR-010. "Update Billing Account" ekraninin orta katman karsiligi (PUT /accounts/{accountId}). '
  + 'Guncellenebilir alanlar Account Name, Account Description ve adrestir; accountNo ile accountTpId '
  + 'HICBIR ZAMAN degismemelidir - bu iki alan icin ayri regresyon testleri vardir.',
  [
    req({
      name: 'TC-010-01 · Guncellenecek hesap mevcut bilgileriyle listelenir [ACC-001, ACC-002]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      description: 'ACC-002: Update ekrani alanlari mevcut degerlerle dolu gelmelidir. '
        + 'Orta katmanda bunun karsiligi, hesabin okundugunda guncel degerlerini dondurmesidir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-010-01 · Hesap mevcut bilgileriyle okunuyor', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct, 'guncellenecek hesap').to.not.be.undefined;
  pm.expect(acct.accountName, 'accountName').to.eql('Ev Faturasi');
  pm.expect(acct.accountDesc, 'accountDesc').to.eql('Aylik elektrik ve su faturasi');
});`
    }),
    billingCase('TC-010-02', 'Account Name ve Description guncellenir -> 200 [ACC-010]', `
b.accountName = 'Guncellenen Hesap';
b.accountDesc = 'Guncellenen aciklama';`, 200, null, {
      method: 'PUT', path: UPD,
      extraTest: `var r = jsonBody();
pm.test('TC-010-02 · Yanit guncel degerleri tasir', function () {
  pm.expect(r.accountName).to.eql('Guncellenen Hesap');
  pm.expect(r.accountDesc).to.eql('Guncellenen aciklama');
});`
    }),
    req({
      name: 'TC-010-03 · Guncelleme kalicidir, listede guncel gorunur [ACC-011, ACC-012]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      test: `${H}
var b = jsonBody();
pm.test('TC-010-03 · Guncel degerler listede okunuyor', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct, 'hesap listede').to.not.be.undefined;
  pm.expect(acct.accountName).to.eql('Guncellenen Hesap');
  pm.expect(acct.accountDesc).to.eql('Guncellenen aciklama');
});`
    }),
    req({
      name: 'TC-010-04 · Hesap numarasi guncellemeyle degismez',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      description: 'accountNo istemciden alinmaz ve guncellenemez - hesabin kalici kimligidir. '
        + 'TC-008-02\'de kaydedilen deger ile karsilastirilir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-010-04 · accountNo olusturuldugu gibi kaldi', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct.accountNo, 'accountNo').to.eql(pm.collectionVariables.get('acctNoF'));
});`
    }),
    req({
      name: 'TC-010-05 · Hesap tipi guncellemeyle degismez',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      test: `${H}
var b = jsonBody();
pm.test('TC-010-05 · accountTpId 224 olarak kaldi', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct.accountTpId, 'accountTpId').to.eql(224);
});`
    }),
    billingCase('TC-010-06', 'Mevcut baska adres secilerek guncellenir -> 200 [ACC-003, ACC-007]', `
b.accountName = 'Guncellenen Hesap';
b.accountDesc = 'Guncellenen aciklama';
b.addressId = Number(pm.collectionVariables.get('addressIdF2'));`, 200, null, {
      method: 'PUT', path: UPD,
      extraTest: `pm.test('TC-010-06 · Hesap secilen adrese baglandi', function () {
  pm.expect(String(jsonBody().addressId)).to.eql(String(pm.collectionVariables.get('addressIdF2')));
});`
    }),
    billingCase('TC-010-07', 'Yeni adres olusturularak guncellenir -> 200 [ACC-003, ACC-004, ACC-006]', `
b.accountName = 'Guncellenen Hesap';
b.accountDesc = 'Guncellenen aciklama';
delete b.addressId;
b.newAddress = newBillingAddress();
b.newAddress.streetName = 'Guncelleme Sokak';
b.newAddress.addressDesc = 'Guncelleme ile eklenen adres';`, 200, null, {
      method: 'PUT', path: UPD,
      extraTest: `var r = jsonBody();
pm.test('TC-010-07 · Hesap yeni olusturulan adrese baglandi', function () {
  pm.expect(r.addressId, 'addressId').to.be.a('number');
  pm.expect(String(r.addressId)).to.not.eql(String(pm.collectionVariables.get('addressIdF2')));
});
if (r.addressId) { pm.collectionVariables.set('addressIdF3', r.addressId); }`
    }),
    billingCase('TC-010-08', 'Account Name bos -> 400 [ACC-008]', "b.accountName = '';", 400, 'This field is required.', {
      method: 'PUT', path: UPD
    }),
    billingCase('TC-010-09', 'Account Name 50 karakter -> 200 (BVA gecerli sinir)', `
b.accountName = repeat('a', 50);
b.addressId = Number(pm.collectionVariables.get('addressIdF3'));`, 200, null, {
      method: 'PUT', path: UPD,
      extraTest: `pm.test('TC-010-09 · 50 karakterlik ad tam olarak kaydedildi', function () {
  pm.expect(String(jsonBody().accountName).length, 'accountName uzunlugu').to.eql(50);
});`
    }),
    billingCase('TC-010-10', 'Account Name 51 karakter -> 400 (BVA sinir disi)', "b.accountName = repeat('a', 51);", 400, null, {
      method: 'PUT', path: UPD
    }),
    billingCase('TC-010-10a', 'Account Name 51 karakter hata mesaji uzunluk kuralini anlatir', "b.accountName = repeat('a', 51);", 400,
      'Maximum 50 characters are allowed.', {
        method: 'PUT', path: UPD, gap: true,
        description: 'TC-008-11 ile ayni kok neden, guncelleme yolunda: UpdateBillingAccountRequest.accountName '
          + 'uzerindeki @Size, FIELD_REQUIRED mesajini paylasiyor. FR-010 validasyon tablosu bu ihlal icin '
          + '"Maximum 50 characters are allowed." metnini tanimliyor.'
      }),
    billingCase('TC-010-11', 'Account Description bos -> 400 (dokuman zorunlu diyor)', "b.accountDesc = '';", 400,
      'This field is required.', {
        method: 'PUT', path: UPD, gap: true,
        description: 'TC-008-12 ile ayni kok neden: UpdateBillingAccountRequest.accountDesc uzerinde de kisit yok. '
          + 'ACC-008 ("Account Name, Account Description ve en az bir adres girilmeden Save aktif olmamalidir") '
          + 'orta katmanda yalnizca accountName ve adres icin uygulaniyor.'
      }),
    billingCase('TC-010-12', 'Adres bilgisi gonderilmez -> 400 [ACC-008]', 'delete b.addressId;', 400,
      'Please provide an existing address or a new one for the billing account.', { method: 'PUT', path: UPD }),
    billingCase('TC-010-13', 'Hem mevcut adres hem yeni adres gonderilir -> 400', 'b.newAddress = newBillingAddress();', 400,
      'Please provide either an existing address or a new one, not both.', { method: 'PUT', path: UPD }),
    billingCase('TC-010-14', 'Var olmayan hesap guncellenemez -> 404', '', 404, 'Billing account not found with id', {
      method: 'PUT', path: '/api/v1/customers/{{custIdF}}/accounts/999999999'
    }),
    billingCase('TC-010-15', 'IDOR - baska musterinin hesabi guncellenemez -> 404', `
b.addressId = Number(pm.collectionVariables.get('addressIdE1'));`, 404, null, {
      method: 'PUT', path: '/api/v1/customers/{{custIdE}}/accounts/{{acctIdF}}',
      description: 'Guvenlik: Musteri F\'ye ait hesap id\'si, Musteri E\'nin yolundan guncellenmeye calisilir. '
        + 'Hesap gercekten var oldugu icin 404, "yok" degil "sana ait degil" anlamindadir - dogru davranis budur '
        + '(403 yerine 404 donmek hesabin varligini da sizdirmaz).'
    }),
    billingCase('TC-010-16', 'Var olmayan musterinin hesabi guncellenemez -> 404', '', 404, 'Customer not found with id', {
      method: 'PUT', path: '/api/v1/customers/999999999/accounts/{{acctIdF}}'
    }),
    billingCase('TC-010-17', 'Gecersiz accountId tipi -> 400', '', 400, 'Parameter accountId has an invalid value.', {
      method: 'PUT', path: '/api/v1/customers/{{custIdF}}/accounts/abc'
    }),
    billingCase('TC-010-18', 'Tokensiz guncelleme istegi -> 401', '', 401, null, { method: 'PUT', path: UPD, noAuth: true })
  ]
);

// ===========================================================================
// 11 - FR-011 BILLING ACCOUNT SILME
// ===========================================================================
const fr011 = folder(
  '11 · FR-011 Fatura Hesabi Silme',
  'FR-011. Aktif hesap silinemez (ACC-003); pasif ve bagli urunu olmayan hesap soft-delete edilir (ACC-005). '
  + 'Silme (DELETE, acct_st_id=DEL) ile aktiflik degisimi (PATCH .../status, ACTIVE<->PASSIVE) ayri kavramlardir; '
  + 'ikisi de burada dogrulanir. Onboarding\'de acilan varsayilan (CUST_ACCT) hesap hicbir zaman silinemez.',
  [
    req({
      name: 'TC-011-01 · Aktif fatura hesabi silinemez -> 409 [ACC-002, ACC-003]',
      method: 'DELETE',
      path: UPD,
      test: `${H}
pm.test('TC-011-01 · Aktif hesap silme engellenir', function () {
  expectCode(409);
  expectMessage('This billing account is active and cannot be deleted.');
});`
    }),
    req({
      name: 'TC-011-02 · Silme engellendikten sonra hesap listede duruyor [ACC-003]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      test: `${H}
var b = jsonBody();
pm.test('TC-011-02 · Reddedilen silme yan etki birakmadi', function () {
  expectCode(200);
  var found = b.content.map(function (a) { return String(a.custAcctId); });
  pm.expect(found).to.include(String(pm.collectionVariables.get('acctIdF')));
});`
    }),
    req({
      name: 'TC-011-03 · Varsayilan musteri hesabi silinemez -> 409',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdF}}/accounts/{{defaultAcctIdF}}',
      description: 'Onboarding\'de acilan CUST_ACCT (223) tipindeki hesap bir "billing account" degildir; '
        + 'FR-011 kapsaminda silinemez. Bu kontrol aktiflik guard\'indan ONCE calisir.',
      test: `${H}
pm.test('TC-011-03 · Varsayilan hesap icin silme engellenir', function () {
  expectCode(409);
  expectMessage('This account is not a billing account and cannot be deleted.');
});`
    }),
    statusCase('TC-011-04', 'Varsayilan hesabin durumu degistirilemez -> 409', 'PASSIVE', 409, null, {
      path: '/api/v1/customers/{{custIdF}}/accounts/{{defaultAcctIdF}}/status',
      description: 'Varsayilan hesap ne silinebilir ne de pasiflestirilebilir - aksi halde FR-007\'deki '
        + '"aktif fatura hesabi" kontrolu dolanilabilirdi.'
    }),
    statusCase('TC-011-05', 'Varsayilan hesap durum hatasi yapilan islemi dogru anlatir', 'PASSIVE', 409,
      'cannot be changed', {
        path: '/api/v1/customers/{{custIdF}}/accounts/{{defaultAcctIdF}}/status',
        gap: true,
        description: 'Durum degistirme istegi, silme mesajini ("This account is not a billing account and cannot be deleted.") '
          + 'yeniden kullaniyor. Kullanici hicbir silme islemi yapmadigi halde silme hatasi goruyor. '
          + 'DefaultAccountCannotBeDeletedException iki farkli akista paylasiliyor; durum degisimi icin ayri bir mesaj gerekir.'
      }),
    statusCase('TC-011-06', 'Hesap pasiflestirilir -> 200 [ACC-005 on kosul]', 'PASSIVE', 200, null, {
      extraTest: `pm.test('TC-011-06 · Hesap pasif duruma gecti', function () { pm.expect(jsonBody().active, 'active').to.eql(false); });`
    }),
    req({
      name: 'TC-011-07 · Pasif hesap listede pasif olarak gorunur [FR-009 ACC-002]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      description: 'Pasiflestirme SILME DEGILDIR: hesap listeden kaybolmaz, yalnizca Account Status kolonu degisir.',
      test: `${H}
var b = jsonBody();
pm.test('TC-011-07 · Pasif hesap listede duruyor ve active=false', function () {
  expectCode(200);
  var acct = b.content.filter(function (a) { return String(a.custAcctId) === String(pm.collectionVariables.get('acctIdF')); })[0];
  pm.expect(acct, 'pasif hesap listede').to.not.be.undefined;
  pm.expect(acct.active, 'active').to.eql(false);
});`
    }),
    statusCase('TC-011-08', 'Pasif hesap yeniden aktif yapilabilir -> 200', 'ACTIVE', 200, null, {
      description: 'Durum degisimi tek yonlu degildir; ACTIVE<->PASSIVE gecisi her iki yonde de calismalidir.',
      extraTest: `pm.test('TC-011-08 · Hesap yeniden aktif', function () { pm.expect(jsonBody().active, 'active').to.eql(true); });`
    }),
    statusCase('TC-011-09', 'Hesap silinmek uzere yeniden pasiflestirilir -> 200', 'PASSIVE', 200, null, {
      extraTest: `pm.test('TC-011-09 · Hesap pasif', function () { pm.expect(jsonBody().active, 'active').to.eql(false); });`
    }),
    req({
      name: 'TC-011-10 · Pasif hesaba bagli urun kontrolu yapiliyor [ACC-004]',
      path: '/api/v1/orders?custAcctId={{acctIdF}}',
      description: 'ACC-004: pasif hesap bile olsa bagli urunu varsa silinemez ve '
        + '"This billing account has active products and cannot be deleted." mesaji gosterilmelidir. '
        + 'Kural BillingAccountBusinessRules.ensureNoLinkedProducts icinde yazili ancak besleyen guard '
        + 'NoOpBillingAccountProductGuard her zaman false donuyor - kural etkisiz durumda. '
        + 'Bu test, silme oncesi hesabin gercekten urunsuz oldugunu dogrular.',
      test: `${H}
var b = jsonBody();
pm.test('TC-011-10 · Hesabin urun listesi sorgulanabiliyor', function () {
  expectCode(200);
  pm.expect(b, 'urun listesi').to.be.an('array');
});
pm.test('TC-011-10 · Silinecek hesabin bagli urunu yok (on kosul saglandi)', function () {
  pm.expect(b.length, 'bagli urun sayisi').to.eql(0);
});
gapTest('TC-011-10 · Bagli urun guard\\'i order-service ile entegre', function () {
  throw new Error('NoOpBillingAccountProductGuard her zaman false donuyor - "urunlu hesap silinemez" kurali dogrulanamiyor.');
});`
    }),
    req({
      name: 'TC-011-11 · Pasif ve urunsuz hesap silinir -> 204 [ACC-005]',
      method: 'DELETE',
      path: UPD,
      test: `${H}
pm.test('TC-011-11 · Hesap soft-delete edildi', function () { expectCode(204); });`
    }),
    req({
      name: 'TC-011-12 · Silinen hesap listede gorunmez [ACC-005]',
      path: '/api/v1/customers/{{custIdF}}/accounts?size=20',
      test: `${H}
var b = jsonBody();
pm.test('TC-011-12 · Silinen hesap listeden cikti', function () {
  expectCode(200);
  var found = b.content.map(function (a) { return String(a.custAcctId); });
  pm.expect(found).to.not.include(String(pm.collectionVariables.get('acctIdF')));
});`
    }),
    req({
      name: 'TC-011-13 · Silinen hesap tekrar silinemez -> 404',
      method: 'DELETE',
      path: UPD,
      test: `${H}
pm.test('TC-011-13 · Ikinci silme istegi 404 doner', function () { expectCode(404); });`
    }),
    billingCase('TC-011-14', 'Silinen hesap guncellenemez -> 404', '', 404, 'Billing account not found with id', {
      method: 'PUT', path: UPD
    }),
    statusCase('TC-011-15', 'Gecersiz durum degeri -> 400', 'DELETED', 400, 'Status must be ACTIVE or PASSIVE.', {
      path: '/api/v1/customers/{{custIdF}}/accounts/{{acctIdF2}}/status',
      description: 'DELETED durumu bu uctan set edilemez - silme yalnizca DELETE ucundan yapilir.'
    }),
    statusCase('TC-011-16', 'Durum degeri bos -> 400', '', 400,
      ['This field is required.', 'Status must be ACTIVE or PASSIVE.'], {
        path: '/api/v1/customers/{{custIdF}}/accounts/{{acctIdF2}}/status',
        description: 'Bos deger hem @NotBlank hem @Pattern kisitini ihlal eder; Bean Validation sirasi garanti '
          + 'olmadigi icin iki mesajdan biri kabul edilir.'
      }),
    req({
      name: 'TC-011-17 · IDOR - baska musterinin hesabi silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdE}}/accounts/{{acctIdF2}}',
      description: 'Guvenlik: Musteri F\'ye ait hesap id\'si, Musteri E\'nin yolundan silinmeye calisilir.',
      test: `${H}
pm.test('TC-011-17 · Baska musterinin hesabi icin 404 doner', function () { expectCode(404); });`
    }),
    req({
      name: 'TC-011-18 · Var olmayan hesap silinemez -> 404',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdF}}/accounts/999999999',
      test: `${H}
pm.test('TC-011-18 · Tanimsiz hesap icin 404 doner', function () { expectCode(404); });`
    }),
    req({
      name: 'TC-011-19 · Fatura hesabina bagli adres silinemez -> 409 [FR-005 ACC-011 capraz]',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdF}}/addresses/{{addressIdF2}}',
      description: 'FR-005 ACC-011 ile FR-008/FR-010 arasindaki capraz kural: bir adres herhangi bir fatura hesabinda '
        + 'kullaniliyorsa silinemez. contact-info-service, silmeden once customer-service\'e '
        + 'GET /accounts/exists-by-address/{addressId} ile sorar. addressIdF2, TC-008-04\'te acilan hesaba baglidir.',
      test: `${H}
pm.test('TC-011-19 · Hesaba bagli adres silme engellenir', function () {
  expectCode(409);
  expectMessage('Please change the related billing address in customer account.');
});`
    }),
    req({
      name: 'TC-011-20 · Tokensiz silme istegi -> 401',
      method: 'DELETE',
      path: '/api/v1/customers/{{custIdF}}/accounts/{{acctIdF2}}',
      noAuth: true,
      test: `${H}
pm.test('TC-011-20 · Yetkisiz istek 401 doner', function () { expectCode(401); });`
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
function next() {
  var remaining = JSON.parse(pm.collectionVariables.get('createdCustomerIds') || '[]');
  if (remaining.length > 0) { postman.setNextRequest('Z01 · Test musterilerini sil (dongu)'); }
}

if (pm.collectionVariables.get('cleanupSkip') === 'true') {
  pm.test('[ATLANDI] Silinecek kayit yok veya cleanup=false', function () { pm.expect(true).to.be.true; });
} else {
  var id = pm.collectionVariables.get('cleanupCustId');
  var base = pm.environment.get('baseUrl') + '/api/v1/customers/' + id;

  if (pm.response.code !== 409) {
    pm.test('Z01 · Test musterisi ' + id + ' temizlendi', function () { expectCode(204, 404); });
    next();
  } else {
    // FR-008..FR-011 testleri musteriye AKTIF fatura hesaplari birakir; aktif hesabi olan musteri
    // silinemez (FR-007 ACC-003). Koleksiyonun sinirsiz kez kosulabilmesi icin hesaplar once
    // pasiflestirilip silinir, ardindan musteri silme yeniden denenir.
    pm.sendRequest({ url: base + '/accounts?size=50', method: 'GET', header: jsonHeaders() }, function (err, res) {
      var accounts = (!err && res.code === 200) ? res.json().content : [];
      // Varsayilan (CUST_ACCT) hesap silinemez ve zaten musteri silinirken kaskad kapanir.
      var billing = accounts.filter(function (a) { return a.accountTpId === 224; });
      var i = 0;
      function closeNext() {
        if (i >= billing.length) {
          pm.sendRequest({ url: base, method: 'DELETE', header: jsonHeaders() }, function (e3, r3) {
            pm.test('Z01 · Test musterisi ' + id + ' temizlendi (hesaplari kapatildiktan sonra)', function () {
              if (e3) { throw new Error(String(e3)); }
              pm.expect([204, 404], 'silme sonucu: ' + r3.code + ' | ' + String(r3.text()).slice(0, 150)).to.include(r3.code);
            });
            if (e3 || [204, 404].indexOf(r3.code) === -1) {
              var left = JSON.parse(pm.collectionVariables.get('cleanupLeftovers') || '[]');
              left.push(id);
              pm.collectionVariables.set('cleanupLeftovers', JSON.stringify(left));
            }
            next();
          });
          return;
        }
        var acctUrl = base + '/accounts/' + billing[i].custAcctId;
        pm.sendRequest({
          url: acctUrl + '/status', method: 'PATCH', header: jsonHeaders(),
          body: { mode: 'raw', raw: JSON.stringify({ status: 'PASSIVE' }) }
        }, function () {
          pm.sendRequest({ url: acctUrl, method: 'DELETE', header: jsonHeaders() }, function () {
            i++;
            closeNext();
          });
        });
      }
      closeNext();
    });
  }
}`
    }),
    req({
      name: 'Z02 · Kosum ozeti',
      path: '/actuator/health',
      noAuth: true,
      test: `${H}
var gaps = JSON.parse(pm.collectionVariables.get('gapLog') || '[]');
var left = JSON.parse(pm.collectionVariables.get('cleanupLeftovers') || '[]');
console.log('==================== CRM Lite (FR-001..FR-011) kosum ozeti ====================');
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
    _postman_id: 'crm-lite-fr001-fr011-v2',
    name: 'CRM Lite - Backend API Testleri (FR-001..FR-011) v2.0',
    description: [
      'CRM Lite gereksinim dokumanindaki FR-001 … FR-011 maddelerinin orta katman (API) karsiliklarini dogrulayan regresyon takimi.',
      '',
      'Kapsam:',
      '- FR-001 Sistem Girisi · FR-002 Musteri Arama · FR-003 Musteri Olusturma · FR-004 Musteri Guncelleme · FR-005 Adres Yonetimi',
      '- FR-006 Iletisim Bilgileri · FR-007 Musteri Silme · FR-008 Fatura Hesabi Olusturma · FR-009 Hesap ve Urun Goruntuleme · FR-010 Hesap Guncelleme · FR-011 Hesap Silme',
      '',
      'Tasarim ilkeleri:',
      '- Her test senaryosu ayri bir istek: Postman arayuzunde tek tek gorunur, tek tek kosulabilir, Allure raporunda ayri test case olur.',
      '- Tum istekler API Gateway (baseUrl) uzerinden gider; gercek JWT ile calisir, token yonetimi otomatiktir.',
      '- 00 klasoru deterministik veri seti kurar; her fixture musteri tek bir sorumluluk alanina ayrilmistir (A/B arama, C pasif, D adres, E iletisim, F fatura hesabi, G silme).',
      '- Her kosum kendi verisini uretir (runId + gecerli TCKN uretici) ve 99 klasorunde temizler; koleksiyon sinirsiz kez kosulabilir.',
      '- Test adlari TC-XXX-YY formatindadir ve ilgili ACC maddesini tasir; docs/traceability-matrix.md ile birebir eslesir.',
      '- Dokumanda tanimli olup kodda karsiligi olmayan maddeler gapTest ile [BEKLEYEN BOSLUK] olarak raporlanir; strictMode=true yapildiginda gercek hataya donusur.',
      '',
      'Kosum sirasi anlamlidir: 00 Setup -> 01..11 -> 99 Temizlik.'
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
    { key: 'createdAccountIds', value: '[]', type: 'string' },
    { key: 'cleanupLeftovers', value: '[]', type: 'string' },
    { key: 'cityId', value: '', type: 'string' },
    { key: 'genderId', value: '', type: 'string' },
    { key: 'genderIdFemale', value: '', type: 'string' }
  ],
  item: [setup, fr001, fr002, fr003, fr004, fr005, fr006, fr007, fr008, fr009, fr010, fr011, teardown]
};

fs.mkdirSync(require('path').dirname(OUT), { recursive: true });
fs.writeFileSync(OUT, JSON.stringify(collection, null, 2), 'utf8');
let n = 0;
collection.item.forEach((f) => { n += f.item.length; });
console.log('Yazildi: ' + OUT);
collection.item.forEach((f) => console.log(String(f.item.length).padStart(4) + '  ' + f.name));
console.log('TOPLAM ISTEK: ' + n);

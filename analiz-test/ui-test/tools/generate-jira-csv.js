#!/usr/bin/env node
/**
 * UI testlerinden Xray Test Case Importer CSV'si uretir.
 *
 * Kaynak: target/allure-results (son kosumun sonuclari)
 * Cikti : ../../jira/CRM-Lite-UI-FR001-FR018-xray.csv   (UTF-8, BOM'suz, virgul)
 *         ../../jira/CRM-Lite-UI-FR001-FR018-excel.csv  (UTF-8 BOM'lu, noktali virgul)
 *
 * Sutun duzeni API'deki analiz-test/postman/tools/generate-jira-csv.js ile BIREBIR
 * AYNIDIR - Jira'da tek standart olsun ve mevcut alan eslestirmesi degismesin diye.
 *
 * Kullanim:
 *   cd analiz-test/ui-test
 *   ./mvnw test -Dsuite=regression     # once kos (Son Kosum sutunu buradan gelir)
 *   node tools/generate-jira-csv.js
 */
const fs = require('fs');
const path = require('path');

const RESULTS = path.join(__dirname, '..', 'target', 'allure-results');
const OUTDIR = path.join(__dirname, '..', '..', 'jira');

const COLUMNS = ['TCID', 'Summary', 'Description', 'Action', 'Data', 'Result', 'Test Type',
    'Priority', 'Labels', 'Component', 'Test Repository Path', 'Test Tipi',
    'Son Kosum', 'Iliskili Bulgu'];

const labelOf = (j, n) => ((j.labels || []).find(l => l.name === n) || {}).value || '';
const linkOf = (j, t) => ((j.links || []).find(l => l.type === t) || {}).name || '';

const PRIORITY = { blocker: 'High', critical: 'High', normal: 'Medium', minor: 'Low', trivial: 'Low' };
const STATUS = { passed: 'Gecti', failed: 'Kaldi', broken: 'Kaldi', skipped: 'Atlandi' };

// Senaryo tipi sezgisi: basligin dilinden cikarilir, KESIN DEGILDIR.
const NEG = /gecersiz|hatali|bos|reddedil|engellen|pasif|kalmalidir|olmamali|uyari|hata mesaji|asamaz|kabul edilmez|silinemez|disabled|bulunamad/i;
const BVA = /sinir|maksimum|en fazla|en az|50 karakter|11 haneli|10 haneli|ust sinir|alt sinir|limit/i;

function testType(title) {
    if (BVA.test(title)) return 'Sinir Deger';
    if (NEG.test(title)) return 'Negatif';
    return 'Pozitif';
}

/** "UI-FR005-02 | Baslik" -> { tcid, title } */
function splitName(name) {
    const i = name.indexOf('|');
    if (i < 0) return { tcid: name.trim(), title: name.trim() };
    return { tcid: name.slice(0, i).trim(), title: name.slice(i + 1).trim() };
}

function csvCell(v, sep) {
    const s = String(v == null ? '' : v).replace(/\r?\n/g, ' ').replace(/\s+/g, ' ').trim();
    return (s.indexOf(sep) >= 0 || s.indexOf('"') >= 0) ? '"' + s.replace(/"/g, '""') + '"' : s;
}

if (!fs.existsSync(RESULTS)) {
    console.error('HATA: allure-results bulunamadi: ' + RESULTS);
    console.error('Once testleri kosun: ./mvnw test -Dsuite=regression');
    process.exit(1);
}

const latest = new Map();   // fullName -> en son sonuc
const cases = new Map();    // fullName -> Set(veri gudumlu vaka etiketleri)

for (const f of fs.readdirSync(RESULTS).filter(x => x.endsWith('-result.json'))) {
    let j;
    try { j = JSON.parse(fs.readFileSync(path.join(RESULTS, f), 'utf8')); } catch (e) { continue; }
    // Cerceve oz-kontrolleri (LocatorSanity, SelfCheck...) test senaryosu degildir, disarida.
    if (!j.fullName || !/^UI-(FR\d+|E2E)/.test(j.name || '')) continue;

    const prev = latest.get(j.fullName);
    if (!prev || (j.start || 0) > (prev.start || 0)) latest.set(j.fullName, j);

    // arg0 = ValidationDataProvider'dan gelen vaka etiketi. Allure.parameter ile eklenen
    // teshis degerleri (Customer ID vb.) vaka DEGILDIR, disarida birakilir.
    for (const p of j.parameters || []) {
        if (p.name === 'arg0' && p.value) {
            if (!cases.has(j.fullName)) cases.set(j.fullName, new Set());
            cases.get(j.fullName).add(String(p.value).replace(/^"|"$/g, ''));
        }
    }
}

const rows = [];
for (const j of latest.values()) {
    const sn = splitName(j.name);
    const epic = labelOf(j, 'epic');
    const story = labelOf(j, 'story');
    const severity = labelOf(j, 'severity');
    const tms = linkOf(j, 'tms');
    const frMatch = epic.match(/FR-\d+/) || sn.tcid.match(/FR\d+/);
    const frCode = frMatch ? frMatch[0].replace(/^FR(\d)/, 'FR-$1') : 'E2E';
    const frName = epic.replace(/^FR-\d+\s*/, '') || 'Uctan Uca';
    const desc = (j.description || '').replace(/\s+/g, ' ').trim();
    const caseList = cases.has(j.fullName) ? Array.from(cases.get(j.fullName)) : [];

    // Description: otomasyon bilgisi EN BASTA olsun ki "Manual" yazisini goren kisi
    // ilk cumlede testin otomatik oldugunu ve kodun yerini gorsun.
    const d = [];
    d.push('OTOMATIK TEST - Selenium + TestNG. Kod: ' + j.fullName.replace(/\.([^.]+)$/, '#$1'));
    d.push('Gereksinim: ' + (epic || 'Uctan uca kritik yol') +
        (story && story.indexOf('—') >= 0 ? ' (' + story.split('—')[0].trim() + ')' : ''));
    if (tms) d.push('Izlenebilirlik: ' + tms);
    d.push('On kosul: Tum servisler ve frontend ayakta olmalidir; test kendi verisini uretir.');
    if (desc) d.push('Not: ' + desc);
    if (caseList.length) d.push('Veri gudumlu ' + caseList.length + ' vaka kosulur: ' + caseList.join(' ; '));

    const storyText = story.indexOf('—') >= 0
        ? story.split('—').slice(1).join('-').trim()
        : story;
    const result = storyText
        ? storyText.charAt(0).toUpperCase() + storyText.slice(1) + (tms ? ' [' + tms + ']' : '')
        : sn.title;

    rows.push({
        'TCID': sn.tcid,
        'Summary': sn.tcid + ' · [OTOMATIK] ' + sn.title,
        'Description': d.join(' || '),
        'Action': sn.title,
        'Data': caseList.length
            ? caseList.length + ' farkli veri seti (bkz. Description)'
            : 'Test kendi on kosulunu ve verisini uretir (rastgele gecerli musteri/adres/iletisim)',
        'Result': result,
        'Test Type': 'Manual',
        'Priority': PRIORITY[severity] || 'Medium',
        'Labels': [frCode, 'otomasyon', 'selenium', 'ui-testi',
            testType(sn.title).toLowerCase().replace(' ', '-')].join(' '),
        'Component': frName,
        'Test Repository Path': 'CRM Lite/UI/' + (epic || 'E2E Uctan Uca Yolculuk'),
        'Test Tipi': testType(sn.title),
        'Son Kosum': STATUS[j.status] || j.status || '',
        'Iliskili Bulgu': ''
    });
}

rows.sort((a, b) => a.TCID.localeCompare(b.TCID, 'tr', { numeric: true }));

function write(file, sep, bom) {
    const lines = [COLUMNS.map(c => csvCell(c, sep)).join(sep)];
    for (const r of rows) lines.push(COLUMNS.map(c => csvCell(r[c], sep)).join(sep));
    fs.writeFileSync(file, (bom ? '﻿' : '') + lines.join('\n') + '\n', 'utf8');
}

fs.mkdirSync(OUTDIR, { recursive: true });
write(path.join(OUTDIR, 'CRM-Lite-UI-FR001-FR018-xray.csv'), ',', false);
write(path.join(OUTDIR, 'CRM-Lite-UI-FR001-FR018-excel.csv'), ';', true);

const byFr = {};
rows.forEach(r => { const k = r.Labels.split(' ')[0]; byFr[k] = (byFr[k] || 0) + 1; });
const byStatus = {};
rows.forEach(r => { byStatus[r['Son Kosum']] = (byStatus[r['Son Kosum']] || 0) + 1; });

console.log('Uretilen test : ' + rows.length);
console.log('Veri gudumlu  : ' + cases.size + ' metot');
console.log('Son kosum     : ' + Object.keys(byStatus).map(k => k + '=' + byStatus[k]).join(', '));
console.log('');
console.log('FR bazinda:');
Object.keys(byFr).sort((a, b) => a.localeCompare(b, 'tr', { numeric: true }))
    .forEach(k => console.log('  ' + k.padEnd(8) + byFr[k]));

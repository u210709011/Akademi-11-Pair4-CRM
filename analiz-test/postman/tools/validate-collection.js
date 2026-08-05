const fs=require('fs');
const c=JSON.parse(fs.readFileSync(require('path').join(__dirname,'..','CRM-Lite-FR001-FR005.postman_collection.json'),'utf8'));
let n=0,bad=0;
function chk(evts,label){(evts||[]).forEach(e=>{const src=e.script.exec.join('\n');n++;try{new Function(src);}catch(err){bad++;console.log('SYNTAX ['+label+'/'+e.listen+']: '+err.message);}});}
chk(c.event,'COLLECTION');
c.item.forEach(f=>f.item.forEach(r=>chk(r.event,f.name+' > '+r.name)));
const pre=c.event.find(e=>e.listen==='prerequest').script.exec.join('\n');
const m=pre.match(/var HELPERS = \[([\s\S]*?)\]\.join/);
const lines=eval('['+m[1]+']');
try{new Function(lines.join('\n'));console.log('HELPERS OK ('+lines.length+' satir)');}catch(e){console.log('HELPERS HATA: '+e.message);bad++;}
console.log('script blogu: '+n+' | hatali: '+bad);

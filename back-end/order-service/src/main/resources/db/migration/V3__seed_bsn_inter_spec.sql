-- Submit Order akisinin referans alacagi en az 1 BSN_INTER_SPEC satiri.
-- bsn_inter_tp_id/prnt_bsn_inter_spec_id bilinclitir NULL birakildi: lookup-service'te
-- (GNL_TP) henuz karsilik gelen bir deger seed edilmedi, karar geregi burada yeni
-- deger acilmiyor.
INSERT INTO bsn_inter_spec (name, descr, shrt_code)
VALUES ('Yeni Satis Siparisi', 'Musteri icin yeni bir satis siparisi baslatma sureci', 'NEW_SALE');

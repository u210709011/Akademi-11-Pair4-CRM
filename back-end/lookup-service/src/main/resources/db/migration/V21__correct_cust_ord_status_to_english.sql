-- V5'te CUST_ORD (siparis durumu, GNL_ST) satirlari Turkce olarak seed edilmisti. Bu uygulamada
-- her string'in varsayilan/temel degeri Ingilizce olmalidir (bkz. V14/V15 ile ayni desen, orada
-- GNL_TP/PARTY_ROLE_TYPE/CUSTOMER icin yapilmisti) - Turkce artik translation tablosunda 'tr'
-- locale'i icin ayri satirlarla saglanir (bkz. V22), taban deger burada duzeltilir.
UPDATE gnl_st SET name = 'Pending', descr = 'Pending'
WHERE ent_code_name = 'CUST_ORD' AND shrt_code = 'WAIT';

UPDATE gnl_st SET name = 'Order Received, Processing', descr = 'Order Received, Processing'
WHERE ent_code_name = 'CUST_ORD' AND shrt_code = 'MIDLWARE';

UPDATE gnl_st SET name = 'Completed', descr = 'Completed'
WHERE ent_code_name = 'CUST_ORD' AND shrt_code = 'FINISHED';

UPDATE gnl_st SET name = 'Rejected', descr = 'Rejected'
WHERE ent_code_name = 'CUST_ORD' AND shrt_code = 'REJECTED';

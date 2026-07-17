import { HttpInterceptorFn } from '@angular/common/http';
//her http isteğini tutup üzerinde değişiklik yapabiliyoruz, localstorageda tokenı okuyup
//headera ekliyoruz, sonra requesti devam ettiriyoruz ki her service istek attığımızda this.http.get(url,{headers yapmayalım})
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: { Authorization: `Bearer ${accessToken}` }
  }));
};

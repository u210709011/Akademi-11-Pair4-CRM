import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';
//her http isteğini tutup üzerinde değişiklik yapabiliyoruz, localstorageda tokenı okuyup
//headera ekliyoruz, sonra requesti devam ettiriyoruz ki her service istek attığımızda this.http.get(url,{headers yapmayalım})
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // 2 kere istek atıyordu  jwt token eski olduğunda bi 200 bi 401 dönüyordu, header eklemek bu istekleri gereksiz yere 401'letir.
  if (req.url.startsWith(environment.authApiUrl)) {
    return next(req);
  }

  const accessToken = localStorage.getItem('accessToken');

  if (!accessToken) {
    return next(req);
  }

  return next(req.clone({
    setHeaders: { Authorization: `Bearer ${accessToken}` }
  }));
};

import {Injectable} from "@angular/core";
import {Certificate} from "app/certificate/certificate";
import {Http, Headers, Response} from "@angular/http";

@Injectable()
export class CertificateService {
  private certificateUrl = 'app/certificate';  // URL to web api

  constructor(private http: Http) {
  }

  getCertificates(): Promise<Certificate[]> {
    return this.http.get(this.certificateUrl)
      .toPromise()
      .then(response => response.json().data as Certificate[])
      .catch(this.handleError);
  }


  getCertificate(id: string): Promise<Certificate> {
    return this.getCertificates()
      .then(certificate => certificate.find(certificate => certificate.id === id));
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }

  // Add new Certificate
  private post(certificate: Certificate): Promise<Certificate> {
    let headers = new Headers({
      'Content-Type': 'application/json'
    });

    return this.http
      .post(this.certificateUrl, JSON.stringify(certificate), {headers: headers})
      .toPromise()
      .then(res => res.json().data)
      .catch(this.handleError);
  }

  // Update existing Certificate
  private put(certificate: Certificate): Promise<Certificate> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');

    let url = `${this.certificateUrl}/${certificate.id}`;

    return this.http
      .put(url, JSON.stringify(certificate), {headers: headers})
      .toPromise()
      .then(() => certificate)
      .catch(this.handleError);
  }

  save(certificate: Certificate): Promise<Certificate> {
    if (certificate.id) {
      return this.put(certificate);
    }
    return this.post(certificate);
  }


}



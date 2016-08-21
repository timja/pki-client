import {Certificate} from "./certificate";
import {Component, OnInit, EventEmitter, Input, Output} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";
import {CertificateService} from "./certificate.service";

// Keep the Input import for now, we'll remove it later:


@Component({
  selector: 'certificate-creator',
  templateUrl: 'app/certificate-detail.component.html',
  styleUrls: ['app/certificate-detail.component.css']
})
export class CertificateDetailComponent implements OnInit {
  @Input() certificate: Certificate;
  @Output() close = new EventEmitter();
  error: any;
  navigated = false; // true if navigated here

  constructor(private certificateService: CertificateService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params.forEach((params: Params) => {
      if (params['id'] !== undefined) {
        let id = params['id'];
        this.navigated = true;
        this.certificateService.getCertificate(id)
          .then(certificate => this.certificate = certificate);
      } else {
        this.navigated = false;
        this.certificate = new Certificate();
      }
    });
  }

  save(): void {
    this.certificateService
      .save(this.certificate)
      .then(certificate => {
        this.certificate = certificate; // saved certificate, w/ id if new
        this.goBack(certificate);
      })
      .catch(error => this.error = error); // TODO: Display error message
  }


  goBack(savedCertificate: Certificate = null): void {
    this.close.emit(savedCertificate);
    if (this.navigated) {
      window.history.back();
    }
  }


}

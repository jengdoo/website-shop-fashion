import { Component, OnInit } from '@angular/core';
@Component({
  selector: 'app-qrcode',
  templateUrl: './qrcode.component.html',
  styleUrl: './qrcode.component.css',
})
export class QrcodeComponent implements OnInit {
  qrCodeUrl: string = '';
  ngOnInit(): void {
    const productDTO = {
      name: 'Sản phẩm B',
      price: 100000,
      quantity: 1000,
      description: 'Mô tả sản phẩm',
      category_id: 2,
    };
    this.generateQRCode(productDTO);
  }
  generateQRCode(productDTO: any) {
    const jsonString = encodeURIComponent(JSON.stringify(productDTO));
    this.qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?data=${jsonString}&size=150x150`;
  }
}

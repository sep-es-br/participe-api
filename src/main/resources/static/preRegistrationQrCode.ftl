<!DOCTYPE html>
<html lang="pt-br">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>QR Code - Audiências Públicas</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 0;
      background-color: #153961 ;
      color:#fff;
    }
    .container {
      width: 400px;
      margin: 0 auto;
      text-align: center;
    }
    .qr-code {
      width: 200px;
      height: 200px;
      margin: 20px auto;
      border-radius: 15px;
    }
    .titulo {
      font-size: 18px;
      font-weight: bold;
      margin-top: 20px;
    }
    .subtitulo {
      font-size: 16px;
      margin-bottom: 20px;
    }
    .informacoes {
      font-size: 14px;
      margin-bottom: 20px;
    }
    .data {
      font-weight: bold;
    }
    .local {
      margin-top: 10px;
    }
    .link {
      font-size: 12px;
      margin-top: 10px;
      text-decoration: none;
      color: #000;
    }
    .link:hover {
      text-decoration: underline;
    }
  </style>
</head>
<body>
  <div class="container">
    <h2 class="titulo"  style="color: #cdcdcd;" >${nomeDaAudiencia}</h2>
    <div style="text-align: center;font-weight: bold;font-size: 1rem;text-transform: uppercase;line-height: 1.5rem;"  style="color: #cdcdcd;">
      Pré-credenciamento<br>Concluído
    </div>
    <div style="text-align:center;margin-top: 1rem;" >
      <p class="informacoes" style="color: #cdcdcd; font-style: italic;" >Apresente o QR Code na entrada e registre sua presença</p>
      <img src="data:image/png;base64,${qrcode}" alt="QR Code - Audiências Públicas" class="qr-code">
    </div>
      <div class="">
        <p style="color: #f59a3e;margin-top: 15px;">Encontro Presencial</p>
        <p style="font-style: italic; color: #cdcdcd;">${microregiao}</p>
        <p style="color: #cdcdcd;">${localDaReuniao}</p>
        <p style="color: #cdcdcd;">${dataDaReuniao}</p>
        <p  style="color: #cdcdcd;">${enderecoDaReuniao}</p>
        <p  style="color: #cdcdcd;">${municipio} - ES</p>
      </div>
  </div>
</body>
</html>

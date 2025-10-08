const form = document.getElementById('uploadForm');
const fileInput = document.getElementById('fileInput');
const statusEl = document.getElementById('status');
const resultEl = document.getElementById('result');
const downloadLink = document.getElementById('downloadLink');
const fileInfo = document.getElementById('fileInfo');
const metaJson = document.getElementById('metaJson');
const showMetaBtn = document.getElementById('showMeta');

form.addEventListener('submit', (e) => {
  e.preventDefault();
  const file = fileInput.files[0];
  if (!file) return alert("Выберите файл");

  statusEl.classList.remove('hidden');
  statusEl.innerHTML = `Загрузка ${file.name} (<span id="percent">0%</span>)<br><progress id="prog" value="0" max="100"></progress>`;

  const xhr = new XMLHttpRequest();
  xhr.open("POST", "/upload", true);

  xhr.upload.onprogress = function(e) {
    if (e.lengthComputable) {
      const percent = Math.round((e.loaded / e.total) * 100);
      document.getElementById('prog').value = percent;
      document.getElementById('percent').innerText = percent + '%';
    }
  };

  xhr.onload = function() {
    statusEl.classList.add('hidden');
    if (xhr.status === 200) {
      const resp = JSON.parse(xhr.responseText);
      const url = resp.downloadUrl;
      downloadLink.href = url;
      downloadLink.textContent = url;
      fileInfo.textContent = `Файл: ${resp.originalName} — ${resp.size} байт`;
      resultEl.classList.remove('hidden');
      metaJson.classList.add('hidden');
      showMetaBtn.onclick = async () => {
        const r = await fetch('/meta/' + resp.token);
        if (r.ok) {
          const m = await r.json();
          metaJson.textContent = JSON.stringify(m, null, 2);
          metaJson.classList.toggle('hidden');
        } else {
          alert('Metadata not found');
        }
      };
    } else {
      alert("Ошибка при загрузке: " + xhr.responseText);
    }
  };

  const fd = new FormData();
  fd.append('file', file);
  xhr.send(fd);
});

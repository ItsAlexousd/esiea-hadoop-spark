const jobConfigs = [
  {
    name: 'AveragePriceByNeighborhood',
    label: 'Prix en euros',
    loaderId: 'loader1',
    chartId: 'chart1',
    noDataId: 'noData1',
    retryBtnId: 'retry1',
    exportPdfId: 'exportPdf1',
    exportCsvId: 'exportCsv1',
  },
  {
    name: 'AveragePriceByRoomType',
    label: 'Prix en euros',
    loaderId: 'loader2',
    chartId: 'chart2',
    noDataId: 'noData2',
    retryBtnId: 'retry2',
    exportPdfId: 'exportPdf2',
    exportCsvId: 'exportCsv2',
  },
  {
    name: 'CountListingsByNeighborhood',
    label: 'Nombre de logements',
    loaderId: 'loader3',
    chartId: 'chart3',
    noDataId: 'noData3',
    retryBtnId: 'retry3',
    exportPdfId: 'exportPdf3',
    exportCsvId: 'exportCsv3',
  },
  {
    name: 'CountListingsByPriceRange',
    label: 'Nombre de logements',
    loaderId: 'loader4',
    chartId: 'chart4',
    noDataId: 'noData4',
    retryBtnId: 'retry4',
    exportPdfId: 'exportPdf4',
    exportCsvId: 'exportCsv4',
  },
  {
    name: 'TopHostsByTotalListings',
    label: 'Nombre de logements',
    loaderId: 'loader5',
    chartId: 'chart5',
    noDataId: 'noData5',
    retryBtnId: 'retry5',
    exportPdfId: 'exportPdf5',
    exportCsvId: 'exportCsv5',
  },
];

// Fonction pour charger les résultats d'un job
function loadJobResults(jobConfig) {
  const {
    label,
    loaderId,
    chartId,
    noDataId,
    retryBtnId,
    exportPdfId,
    exportCsvId,
  } = jobConfig;

  const loader = document.getElementById(loaderId);
  const chartCanvas = document.getElementById(chartId);
  const noDataMessage = document.getElementById(noDataId);
  const retryButton = document.getElementById(retryBtnId);
  const exportPdfButton = document.getElementById(exportPdfId);
  const exportCsvButton = document.getElementById(exportCsvId);

  // Affiche le loader
  loader.style.display = 'block';
  chartCanvas.style.display = 'none'; // Masque le graphique
  noDataMessage.style.display = 'none'; // Masque le message de données non disponibles
  exportPdfButton.style.display = 'none'; // Masque les boutons d'exportation
  exportCsvButton.style.display = 'none'; // Masque les boutons d'exportation

  // Requête API
  fetch(`http://localhost:3000/api/results/${jobConfig.name}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then((data) => {
      // Masque le loader
      loader.style.display = 'none';

      // Vérifie si les données sont vides
      if (data.length === 0) {
        noDataMessage.style.display = 'block'; // Affiche le message de données non disponibles
        return;
      }

      // Prépare les données pour le graphique
      const labels = data.map((item) => item.key);
      const values = data.map((item) => item.value);

      // Crée le graphique
      const chart = new Chart(chartCanvas, {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              label: label,
              data: values,
              backgroundColor: 'rgba(52, 152, 219, 0.5)',
              borderColor: 'rgba(52, 152, 219, 1)',
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
            },
          },
        },
      });

      chartCanvas.style.display = 'block'; // Affiche le graphique
      noDataMessage.style.display = 'none'; // Cache le message de données non disponibles
      retryButton.style.display = 'none'; // Cache le bouton de réessai si la requête réussit

      // Affiche les boutons d'exportation
      exportPdfButton.style.display = 'inline-block';
      exportCsvButton.style.display = 'inline-block';

      // Configure les événements d'exportation
      exportPdfButton.onclick = () => exportChartToPDF(chart);
      exportCsvButton.onclick = () => exportDataToCSV(data);
    })
    .catch((error) => {
      console.error('Erreur lors de la récupération des données:', error);
      loader.style.display = 'none'; // Masque le loader
      noDataMessage.style.display = 'block'; // Affiche le message de données non disponibles
      retryButton.style.display = 'block'; // Affiche le bouton de réessai
      exportPdfButton.style.display = 'none'; // Masque les boutons d'exportation
      exportCsvButton.style.display = 'none'; // Masque les boutons d'exportation
    });
}

// Fonction pour exporter le graphique en PDF
function exportChartToPDF(chart) {
  // Accéder à jsPDF via window.jspdf
  const { jsPDF } = window.jspdf;

  const pdf = new jsPDF('l', 'pt', 'a4'); // Initialise jsPDF
  pdf.text('Graphique Exporté', 40, 30); // Ajoute un titre fixe

  // Convertit le graphique en image
  const imgData = chart.toBase64Image();
  pdf.addImage(imgData, 'PNG', 40, 50, 500, 300); // Ajuste la position et la taille

  // Enregistre le PDF
  pdf.save('chart.pdf');
}

// Fonction pour exporter les données en CSV
function exportDataToCSV(data) {
  const csvRows = [];
  const headers = ['Key', 'Value']; // En-têtes pour le CSV
  csvRows.push(headers.join(',')); // Ajoute les en-têtes

  // Ajoute les données
  for (const item of data) {
    csvRows.push(`${item.key},${item.value}`);
  }

  // Crée un blob et télécharge le fichier
  const blob = new Blob([csvRows.join('\n')], { type: 'text/csv' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.setAttribute('href', url);
  a.setAttribute('download', 'data.csv');
  document.body.appendChild(a);
  a.click(); // Simule le clic
  document.body.removeChild(a); // Retire l'élément
}

// Charger les résultats pour chaque job au démarrage
jobConfigs.forEach((jobConfig) => loadJobResults(jobConfig));

// Événements de clic sur les boutons de réessai
jobConfigs.forEach((jobConfig) => {
  document
    .getElementById(jobConfig.retryBtnId)
    .addEventListener('click', () => loadJobResults(jobConfig));
});

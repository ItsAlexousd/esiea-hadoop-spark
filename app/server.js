const express = require('express');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = 3000;

// Activer CORS pour permettre les requêtes depuis n'importe quelle origine
app.use(cors());

// Servir les fichiers statiques (CSS, JS)
app.use(express.static(path.join(__dirname, 'public')));

// Fonction pour lire tous les résultats d'un job donné
function readJobResults(jobName) {
  const jobDir = path.join('spark_jobs_output', jobName);
  let results = [];

  // Vérifie si le répertoire du job existe
  if (fs.existsSync(jobDir)) {
    // Lire tous les fichiers dans le répertoire
    const files = fs.readdirSync(jobDir);

    files.forEach((file) => {
      // Vérifie que le fichier commence par 'part-'
      if (file.startsWith('part-')) {
        const filePath = path.join(jobDir, file);
        try {
          // Lire le contenu du fichier et l'ajouter au tableau results
          const data = fs.readFileSync(filePath, 'utf-8');
          const lines = data.split('\n');
          // Analyser chaque ligne pour créer des objets JSON
          lines.forEach((line) => {
            if (line.trim()) {
              const [key, value] = line.replace(/[()]/g, '').split(',');
              results.push({
                key: key.trim(),
                value: parseFloat(value.trim()),
              });
            }
          });
        } catch (error) {
          console.error(
            `Erreur lors de la lecture du fichier ${file} pour ${jobName}:`,
            error
          );
        }
      }
    });
  } else {
    console.error(`Le répertoire pour le job ${jobName} n'existe pas.`);
  }

  // Retourne tous les résultats sous forme de tableau JSON
  return results;
}

// Route pour obtenir les résultats d'un job donné
app.get('/api/results/:jobName', (req, res) => {
  const jobName = req.params.jobName;
  const data = readJobResults(jobName);

  if (data.length > 0) {
    res.status(200).json(data);
  } else {
    res
      .status(404)
      .send({ error: `Résultats non trouvés pour le job: ${jobName}` });
  }
});

// Route par défaut
app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Démarrer le serveur
app.listen(PORT, () => {
  console.log(`Serveur backend démarré sur le port ${PORT}`);
});

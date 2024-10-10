# Exploration des données Airbnb à New York

## Table des matières

1. [Membres du projet](#membres-du-projet)
2. [Introduction](#introduction)
3. [Architecture du Système](#architecture-du-système)
   - [Composants](#composants)
4. [Étapes de Configuration](#étapes-de-configuration)
5. [Instructions pour Exécuter le Projet](#instructions-pour-exécuter-le-projet)
6. [Conclusion](#conclusion)

## Membres du projet

Nous sommes 5 étudiants de 5A FISA à l'ESIEA de Laval

| Nom      | Prénom |
|----------|--------|
| Dalle    | Alexis |
| Ladislas | Albin  |
| Lambert  | Matéo  |
| Louis    | Lenny  |
| Ménard   | Malo   |

## Introduction

Ce projet a pour objectif d'analyser et de visualiser des données Airbnb à New York en utilisant une architecture basée sur Hadoop et Spark pour le traitement des données, avec une interface utilisateur développée en HTML, CSS, JavaScript et Express.js pour la présentation et l'interaction. L'ensemble de l'application est conteneurisé avec Docker, simplifiant ainsi le déploiement et la gestion des dépendances.

## Architecture du système

L'architecture du projet repose sur plusieurs composants interconnectés :

### Composants

1. **Hadoop** :
   - Utilisé comme système de fichiers distribué (HDFS) pour le stockage des données volumineuses collectées depuis un dataset Kaggle Airbnb.
   - Comprend un master et deux workers pour gérer le stockage et le traitement des données.

2. **Spark** :
   - Utilisé pour le traitement des données stockées dans HDFS.
   - Facilite l'analyse rapide des données grâce à son modèle de traitement en mémoire, permettant des opérations comme le filtrage, l'agrégation et la transformation des données.

3. **App (HTML/CSS/JS/Express)** :
   - **HTML/CSS** : Création de l'interface utilisateur pour la visualisation des données.
   - **JavaScript** : Utilisé pour rendre la page dynamique, notamment pour afficher les graphiques avec Chart.js.
   - **Express.js** : Serveur web qui sert les fichiers statiques et gère les requêtes API.

4. **Docker** :
   - Conteneurisation de tous les services pour faciliter le déploiement et l'isolement des environnements.
   - Utilisation de `docker-compose` pour orchestrer les différents conteneurs.

## Étapes de configuration

1. **Pré-requis** :
   - Assurez-vous d'avoir Docker installé sur votre machine.

2. **Structure du projet** :
   - Le projet est organisé avec les répertoires suivants :
     ```
     ├── app
     │   └── (Code de l'application Express)
     ├── hadoop
     │   ├── master
     │   │   ├── data
     │   │   │   └─- (Dataset, Scripts bash, Application Spark)
     │   │   └── (Dockerfile et configuration du master Hadoop)
     │   └── worker
     │       └── (Dockerfile et configuration des workers Hadoop)
     ├── spark
     │   └── (Code de l'application Spark)
     ├── docker-compose.yml
     └── README.md
     ```

## Instructions pour exécuter le Projet

1. **Démarrer les services** :
   - Ouvrez un terminal et naviguez jusqu'au répertoire racine du projet.
   - Exécutez la commande suivante pour démarrer tous les services :
     ```bash
     docker compose up -d
     ```

2. **Accéder aux interfaces utilisateur** :
   - Une fois les conteneurs en cours d'exécution, ouvrez votre navigateur web et accédez à `http://localhost:3000` pour l'interface utilisateur Express.
   - Pour accéder à l'interface web HDFS, utilisez `http://localhost:9870`.
   - Pour le ResourceManager de YARN, accédez à `http://localhost:8088`.

3. **Interagir avec l'application** :
   - Utilisez l'interface pour visualiser les données Airbnb et effectuer des analyses.
   - Exportez les résultats en PDF ou CSV à l'aide des fonctionnalités fournies dans l'application.

## Conclusion

Ce projet offre une approche complète pour explorer et analyser les données Airbnb à New York, en utilisant des technologies modernes de traitement de données et de développement web. Grâce à l'intégration de Hadoop, Spark, Express.js, et Docker, il est possible de manipuler de grandes quantités de données tout en offrant une interface utilisateur intuitive pour l'analyse et la visualisation.

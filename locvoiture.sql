-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mer. 16 avr. 2025 à 10:26
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `locvoiture`
--

-- --------------------------------------------------------

--
-- Structure de la table `entretiens`
--

CREATE TABLE `entretiens` (
  `id` int(11) NOT NULL,
  `idVehicule` int(11) NOT NULL,
  `date` date NOT NULL,
  `type` varchar(100) NOT NULL,
  `cout` double NOT NULL,
  `remarques` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `entretiens`
--

INSERT INTO `entretiens` (`id`, `idVehicule`, `date`, `type`, `cout`, `remarques`) VALUES
(1, 1, '2025-05-18', 'Carrosserie', 250, 'COuleur'),
(2, 2, '2025-04-16', 'Frein', 150, 'Disque + plaquette');

-- --------------------------------------------------------

--
-- Structure de la table `reservations`
--

CREATE TABLE `reservations` (
  `id` int(11) NOT NULL,
  `clientName` text NOT NULL,
  `immatriculation` text NOT NULL,
  `startDate` text NOT NULL,
  `endDate` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reservations`
--

INSERT INTO `reservations` (`id`, `clientName`, `immatriculation`, `startDate`, `endDate`) VALUES
(1, 'test', 'BQ-281-SD', '2025-05-10', '2025-05-15');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', '9e68cef6e2fc7e5dc7fa5f6aeff323cf3cedd58a788984bb5340b63fc7110a96', 'DIRECTEUR');

-- --------------------------------------------------------

--
-- Structure de la table `vehicules`
--

CREATE TABLE `vehicules` (
  `id` int(11) NOT NULL,
  `marque` text NOT NULL,
  `modele` text NOT NULL,
  `immatriculation` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `vehicules`
--

INSERT INTO `vehicules` (`id`, `marque`, `modele`, `immatriculation`) VALUES
(1, 'Renault', 'Twingo 2', 'BQ-281-SD'),
(2, 'iufnsjk', 'scsdvdsv', 'BQ-288-44');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `entretiens`
--
ALTER TABLE `entretiens`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idVehicule` (`idVehicule`);

--
-- Index pour la table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Index pour la table `vehicules`
--
ALTER TABLE `vehicules`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `entretiens`
--
ALTER TABLE `entretiens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `entretiens`
--
ALTER TABLE `entretiens`
  ADD CONSTRAINT `entretiens_ibfk_1` FOREIGN KEY (`idVehicule`) REFERENCES `vehicules` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

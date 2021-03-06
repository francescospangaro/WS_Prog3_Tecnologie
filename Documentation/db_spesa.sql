-- phpMyAdmin SQL Dump
-- version 4.9.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mag 09, 2020 alle 18:09
-- Versione del server: 10.4.8-MariaDB
-- Versione PHP: 7.3.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_spesa`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `liste`
--

CREATE TABLE `liste` (
  `idLista` int(11) NOT NULL,
  `rifRichiesta` int(11) NOT NULL,
  `rifProdotto` int(11) NOT NULL,
  `QT` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `liste`
--

INSERT INTO `liste` (`idLista`, `rifRichiesta`, `rifProdotto`, `QT`) VALUES
(1, 1, 1, 1),
(2, 1, 2, 2);

-- --------------------------------------------------------

--
-- Struttura della tabella `prodotti`
--

CREATE TABLE `prodotti` (
  `idProdotto` int(11) NOT NULL,
  `genere` varchar(30) NOT NULL,
  `etichetta` longtext NOT NULL,
  `costo` double(6,2) NOT NULL,
  `nome` varchar(50) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `descrizione` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `prodotti`
--

INSERT INTO `prodotti` (`idProdotto`, `genere`, `etichetta`, `costo`, `nome`, `marca`, `descrizione`) VALUES
(1, 'alimento', '11111', 12.00, 'Salmone', '-', 'congelato'),
(2, 'alimento', '121212', 1.00, 'Ananas', '-', 'Frutta');

-- --------------------------------------------------------

--
-- Struttura della tabella `richieste`
--

CREATE TABLE `richieste` (
  `idRichiesta` int(11) NOT NULL,
  `rifUtente` int(11) NOT NULL,
  `oraInizioConsegna` time NOT NULL,
  `oraFineConsegna` time NOT NULL,
  `durataRichiesta` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `richieste`
--

INSERT INTO `richieste` (`idRichiesta`, `rifUtente`, `oraInizioConsegna`, `oraFineConsegna`, `durataRichiesta`) VALUES
(1, 2, '08:17:00', '23:00:00', '99:00:00'),
(2, 2, '19:00:00', '24:00:00', '99:00:00');

-- --------------------------------------------------------

--
-- Struttura della tabella `risposte`
--

CREATE TABLE `risposte` (
  `idRisposta` int(11) NOT NULL,
  `rifUtente` int(11) NOT NULL,
  `rifRichiesta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Struttura della tabella `utenti`
--

CREATE TABLE `utenti` (
  `idUtente` int(11) NOT NULL,
  `username` varchar(30) NOT NULL,
  `nome` varchar(30) NOT NULL,
  `cognome` varchar(30) NOT NULL,
  `password` varchar(32) NOT NULL,
  `codiceFiscale` varchar(16) NOT NULL,
  `regione` varchar(30) NOT NULL,
  `via` varchar(30) NOT NULL,
  `nCivico` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `utenti`
--

INSERT INTO `utenti` (`idUtente`, `username`, `nome`, `cognome`, `password`, `codiceFiscale`, `regione`, `via`, `nCivico`) VALUES
(1, 'fraGali', 'Francesco', 'Galimberti', '5f4dcc3b5aa765d61d8327deb882cf99', 'GLMFNC01A02B729Q', 'Lombardia', 'giacomo leopardi', '4'),
(2, 'ACaso', 'A', 'Caso', '5f4dcc3b5aa765d61d8327deb882cf99', 'ACASO7182', 'Case', 'via', '3');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `liste`
--
ALTER TABLE `liste`
  ADD PRIMARY KEY (`idLista`);

--
-- Indici per le tabelle `prodotti`
--
ALTER TABLE `prodotti`
  ADD PRIMARY KEY (`idProdotto`);

--
-- Indici per le tabelle `richieste`
--
ALTER TABLE `richieste`
  ADD PRIMARY KEY (`idRichiesta`),
  ADD KEY `associazione1` (`rifUtente`);

--
-- Indici per le tabelle `risposte`
--
ALTER TABLE `risposte`
  ADD PRIMARY KEY (`idRisposta`),
  ADD KEY `associazione2` (`rifUtente`),
  ADD KEY `associazione3` (`rifRichiesta`);

--
-- Indici per le tabelle `utenti`
--
ALTER TABLE `utenti`
  ADD PRIMARY KEY (`idUtente`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `liste`
--
ALTER TABLE `liste`
  MODIFY `idLista` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `prodotti`
--
ALTER TABLE `prodotti`
  MODIFY `idProdotto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `richieste`
--
ALTER TABLE `richieste`
  MODIFY `idRichiesta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `risposte`
--
ALTER TABLE `risposte`
  MODIFY `idRisposta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `utenti`
--
ALTER TABLE `utenti`
  MODIFY `idUtente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `richieste`
--
ALTER TABLE `richieste`
  ADD CONSTRAINT `associazione1` FOREIGN KEY (`rifUtente`) REFERENCES `utenti` (`idUtente`);

--
-- Limiti per la tabella `risposte`
--
ALTER TABLE `risposte`
  ADD CONSTRAINT `associazione2` FOREIGN KEY (`rifUtente`) REFERENCES `utenti` (`idUtente`),
  ADD CONSTRAINT `associazione3` FOREIGN KEY (`rifRichiesta`) REFERENCES `richieste` (`idRichiesta`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 08 Bulan Mei 2025 pada 07.09
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `virtual_cat`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `game_scores`
--

CREATE TABLE `game_scores` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `game_scores`
--

INSERT INTO `game_scores` (`id`, `user_id`, `score`, `date_created`) VALUES
(34, 3, 41, '2025-05-06 08:38:11'),
(35, 6, 16, '2025-05-06 08:41:38'),
(36, 3, 6, '2025-05-06 09:05:37'),
(37, 3, 23, '2025-05-06 09:08:28'),
(38, 3, 0, '2025-05-08 04:21:05'),
(39, 3, 23, '2025-05-08 04:21:33'),
(40, 6, 20, '2025-05-08 04:24:18'),
(41, 3, 2, '2025-05-08 04:27:36'),
(42, 3, 16, '2025-05-08 04:49:15'),
(43, 3, 4, '2025-05-08 04:49:27'),
(44, 3, 0, '2025-05-08 04:49:46');

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `created_at`) VALUES
(3, 'cut', '1', '2025-04-30 04:59:58'),
(4, 'ainal', '1', '2025-04-30 05:04:07'),
(5, 'nazwa', '1', '2025-04-30 05:29:14'),
(6, 'awa', '1', '2025-05-06 08:40:58');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `game_scores`
--
ALTER TABLE `game_scores`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `game_scores`
--
ALTER TABLE `game_scores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `game_scores`
--
ALTER TABLE `game_scores`
  ADD CONSTRAINT `game_scores_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

-- Reviews seed data: ~200 reviews across ~40 products from various users
-- Users 1-50 exist. Orders belong to user 1 (user001), so user001 can review ordered products.
-- For seed data, we insert reviews directly (bypassing the "must have purchased" check).

-- Pâtes Barilla (product 1) - 8 reviews, avg ~4.1
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 1, 5, 'Excellentes pâtes, cuisson parfaite al dente. Je recommande vivement !', '2026-02-25 10:00:00'),
(3, 1, 4, 'Bonnes pâtes classiques, rapport qualité-prix correct.', '2026-02-26 14:30:00'),
(4, 1, 4, 'Très bon produit, je les achète régulièrement.', '2026-03-01 09:15:00'),
(5, 1, 3, 'Pâtes correctes mais rien d''exceptionnel.', '2026-03-02 16:45:00'),
(6, 1, 5, 'Les meilleures pâtes du marché selon moi.', '2026-03-03 11:20:00'),
(7, 1, 4, 'Bonne tenue à la cuisson, idéales pour les sauces.', '2026-03-05 18:00:00'),
(8, 1, 4, 'Produit fiable, toujours de bonne qualité.', '2026-03-06 20:30:00'),
(9, 1, 4, 'Très satisfait de ces pâtes italiennes.', '2026-03-07 12:00:00');

-- Riz basmati (product 2) - 5 reviews, avg ~4.4
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 2, 5, 'Riz parfumé et léger, parfait pour les plats asiatiques.', '2026-02-28 10:00:00'),
(4, 2, 4, 'Bon riz, grains longs et bien séparés après cuisson.', '2026-03-01 14:00:00'),
(6, 2, 5, 'Excellente qualité, le parfum est incroyable.', '2026-03-04 09:30:00'),
(8, 2, 4, 'Très bon rapport qualité-prix pour du basmati.', '2026-03-06 15:00:00'),
(10, 2, 4, 'Bon produit, je le recommande.', '2026-03-08 11:00:00');

-- Huile d''olive (product 3) - 6 reviews, avg ~4.5
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(3, 3, 5, 'Huile fruitée avec un goût prononcé, excellente en salade.', '2026-02-27 10:00:00'),
(5, 3, 5, 'Première pression à froid, on sent la qualité.', '2026-03-01 12:00:00'),
(7, 3, 4, 'Très bonne huile, un peu chère mais ça vaut le coup.', '2026-03-03 14:30:00'),
(9, 3, 4, 'Goût authentique, parfaite pour la cuisine méditerranéenne.', '2026-03-05 16:00:00'),
(11, 3, 5, 'La meilleure huile d''olive que j''ai goûtée.', '2026-03-07 10:00:00'),
(13, 3, 4, 'Très bon produit, je rachèterai.', '2026-03-09 18:00:00');

-- Evian (product 9) - 4 reviews, avg ~3.8
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 9, 4, 'Eau pure et agréable, classique et fiable.', '2026-03-01 10:00:00'),
(4, 9, 3, 'C''est de l''eau... elle fait le job.', '2026-03-02 14:00:00'),
(6, 9, 4, 'Eau très douce, parfaite au quotidien.', '2026-03-04 09:00:00'),
(8, 9, 4, 'Bon prix en promotion, qualité constante.', '2026-03-06 15:00:00');

-- Coca-Cola (product 12) - 7 reviews, avg ~3.9
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 12, 4, 'Le goût classique qu''on connaît tous. Rafraîchissant !', '2026-03-01 10:00:00'),
(3, 12, 5, 'Rien ne remplace un bon Coca bien frais.', '2026-03-02 12:00:00'),
(5, 12, 3, 'Trop sucré pour moi mais le goût est là.', '2026-03-03 14:00:00'),
(7, 12, 4, 'Un classique indémodable.', '2026-03-05 16:00:00'),
(9, 12, 4, 'Parfait pour les barbecues d''été.', '2026-03-06 18:00:00'),
(11, 12, 4, 'Le format 1.5L est idéal pour la famille.', '2026-03-07 10:00:00'),
(13, 12, 3, 'Bon mais je préfère le Pepsi.', '2026-03-08 12:00:00');

-- Bordeaux rouge (product 16) - 6 reviews, avg ~4.3
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 16, 5, 'Excellent Bordeaux pour le prix. Fruité et équilibré.', '2026-03-01 20:00:00'),
(4, 16, 4, 'Bon vin de table, agréable avec de la viande rouge.', '2026-03-03 19:00:00'),
(6, 16, 4, 'Rapport qualité-prix imbattable pour un Bordeaux.', '2026-03-05 21:00:00'),
(8, 16, 5, 'Belle robe, nez fruité, bouche ronde. Très bien !', '2026-03-07 20:30:00'),
(10, 16, 4, 'Agréable surprise, je le recommande.', '2026-03-09 19:00:00'),
(12, 16, 4, 'Bon vin sans prétention, parfait pour le quotidien.', '2026-03-10 20:00:00');

-- Leffe blonde (product 19) - 9 reviews, avg ~4.2
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 19, 5, 'Ma bière préférée ! Douce et fruitée, un délice.', '2026-03-01 18:00:00'),
(3, 19, 4, 'Classique des bières d''abbaye, toujours aussi bonne.', '2026-03-02 19:00:00'),
(4, 19, 4, 'Bonne bière pour l''apéro entre amis.', '2026-03-03 20:00:00'),
(5, 19, 3, 'Un peu trop sucrée à mon goût.', '2026-03-04 18:30:00'),
(6, 19, 5, 'Excellente bière, le format 75cl est top.', '2026-03-05 19:00:00'),
(7, 19, 4, 'Très bon rapport qualité-prix en promotion.', '2026-03-06 20:00:00'),
(8, 19, 4, 'Une valeur sûre pour les soirées.', '2026-03-07 18:00:00'),
(9, 19, 5, 'La meilleure bière blonde du rayon.', '2026-03-08 19:30:00'),
(10, 19, 4, 'Toujours un plaisir de retrouver cette bière.', '2026-03-09 20:00:00');

-- Veste en cuir noire (product 23) - 5 reviews, avg ~4.6
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 23, 5, 'Cuir de très bonne qualité, coupe parfaite. J''adore !', '2026-03-01 10:00:00'),
(4, 23, 5, 'Magnifique veste, le cuir est souple et résistant.', '2026-03-03 14:00:00'),
(6, 23, 4, 'Belle veste mais taille un peu grand, prenez en dessous.', '2026-03-05 11:00:00'),
(8, 23, 5, 'Rapport qualité-prix excellent pour du vrai cuir.', '2026-03-07 16:00:00'),
(10, 23, 4, 'Très satisfait, la veste vieillit bien.', '2026-03-09 10:00:00');

-- T-shirt coton blanc (product 25) - 10 reviews, avg ~4.0
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 25, 4, 'Basique mais de qualité, le coton est doux.', '2026-03-01 10:00:00'),
(3, 25, 5, 'Parfait ! Coton bio, coupe droite, prix mini.', '2026-03-02 12:00:00'),
(4, 25, 3, 'Correct mais rétrécit un peu au lavage.', '2026-03-03 14:00:00'),
(5, 25, 4, 'Bon t-shirt basique, j''en ai pris 3.', '2026-03-04 09:00:00'),
(6, 25, 5, 'Le meilleur t-shirt blanc que j''ai trouvé.', '2026-03-05 11:00:00'),
(7, 25, 3, 'Tissu un peu fin mais agréable à porter.', '2026-03-06 15:00:00'),
(8, 25, 4, 'Bon rapport qualité-prix, surtout en promo.', '2026-03-07 16:00:00'),
(9, 25, 4, 'Satisfait de cet achat, confortable au quotidien.', '2026-03-08 10:00:00'),
(10, 25, 4, 'Simple et efficace, rien à redire.', '2026-03-09 12:00:00'),
(11, 25, 4, 'Je recommande pour un usage quotidien.', '2026-03-10 14:00:00');

-- Clavier mécanique RGB (product 35) - 7 reviews, avg ~4.4
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 35, 5, 'Switches Cherry MX incroyables, frappe précise et agréable.', '2026-03-01 10:00:00'),
(3, 35, 4, 'Très bon clavier pour le gaming et la bureautique.', '2026-03-02 14:00:00'),
(4, 35, 5, 'Le rétroéclairage RGB est magnifique !', '2026-03-03 16:00:00'),
(5, 35, 4, 'Solide et bien construit, je suis satisfait.', '2026-03-04 11:00:00'),
(6, 35, 4, 'Bon clavier mais un peu bruyant en open space.', '2026-03-05 13:00:00'),
(7, 35, 5, 'Parfait pour le travail et le jeu.', '2026-03-06 15:00:00'),
(8, 35, 4, 'Excellent rapport qualité-prix.', '2026-03-07 17:00:00');

-- Casque Bluetooth Sony (product 37) - 8 reviews, avg ~4.6
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 37, 5, 'Réduction de bruit exceptionnelle, son cristallin.', '2026-03-01 10:00:00'),
(3, 37, 5, 'Le meilleur casque que j''ai eu. Autonomie incroyable.', '2026-03-02 12:00:00'),
(4, 37, 4, 'Très bon son mais un peu lourd sur la durée.', '2026-03-03 14:00:00'),
(5, 37, 5, 'Parfait pour le télétravail et les transports.', '2026-03-04 16:00:00'),
(6, 37, 4, 'Son de qualité, ANC efficace, confortable.', '2026-03-05 10:00:00'),
(7, 37, 5, 'J''ai remplacé mon ancien casque et c''est le jour et la nuit.', '2026-03-06 12:00:00'),
(8, 37, 5, 'Qualité Sony au rendez-vous, rien à redire.', '2026-03-07 14:00:00'),
(9, 37, 4, 'Très bon casque, le prix est justifié en promo.', '2026-03-08 16:00:00');

-- Nintendo Switch OLED (product 42) - 6 reviews, avg ~4.5
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 42, 5, 'L''écran OLED est sublime, les couleurs sont vives.', '2026-03-01 10:00:00'),
(3, 42, 5, 'Console parfaite pour jouer en famille.', '2026-03-02 14:00:00'),
(4, 42, 4, 'Très bonne console mais les Joy-Con dérivent toujours...', '2026-03-03 16:00:00'),
(5, 42, 4, 'Excellente en mode portable, l''OLED fait la différence.', '2026-03-04 11:00:00'),
(6, 42, 5, 'Meilleure console portable du marché.', '2026-03-05 13:00:00'),
(7, 42, 4, 'Très bon achat, la bibliothèque de jeux est énorme.', '2026-03-06 15:00:00');

-- Zelda TOTK (product 43) - 12 reviews, avg ~4.8
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 43, 5, 'Chef-d''oeuvre absolu. Le meilleur jeu de la décennie.', '2026-03-01 10:00:00'),
(3, 43, 5, 'Plus de 100h de jeu et toujours des choses à découvrir.', '2026-03-02 12:00:00'),
(4, 43, 5, 'Monde ouvert incroyable, liberté totale.', '2026-03-03 14:00:00'),
(5, 43, 5, 'La physique du jeu est révolutionnaire.', '2026-03-04 16:00:00'),
(6, 43, 4, 'Excellent mais les FPS chutent parfois.', '2026-03-05 10:00:00'),
(7, 43, 5, 'Un jeu qui repousse les limites du genre.', '2026-03-06 12:00:00'),
(8, 43, 5, 'Sublime du début à la fin.', '2026-03-07 14:00:00'),
(9, 43, 5, 'Impossible de s''ennuyer, le contenu est hallucinant.', '2026-03-08 16:00:00'),
(10, 43, 5, 'Mon jeu préféré de tous les temps.', '2026-03-09 10:00:00'),
(11, 43, 4, 'Incroyable mais la durée peut être intimidante.', '2026-03-10 12:00:00'),
(12, 43, 5, 'Un must-have pour tout possesseur de Switch.', '2026-03-11 14:00:00'),
(13, 43, 5, 'Magnifique. Tout simplement magnifique.', '2026-03-12 16:00:00');

-- Lessive Skip (product 48) - 4 reviews, avg ~3.5
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 48, 4, 'Lave bien, parfum agréable et pas trop fort.', '2026-03-01 10:00:00'),
(4, 48, 3, 'Correcte mais ne détache pas les taches tenaces.', '2026-03-03 14:00:00'),
(6, 48, 4, 'Bon produit pour un usage quotidien.', '2026-03-05 10:00:00'),
(8, 48, 3, 'Fait le minimum, rien de plus.', '2026-03-07 16:00:00');

-- Shampooing Elsève (product 55) - 5 reviews, avg ~4.0
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 55, 4, 'Cheveux doux et brillants après utilisation.', '2026-03-01 10:00:00'),
(3, 55, 5, 'Le meilleur shampooing réparateur que j''ai testé.', '2026-03-02 12:00:00'),
(5, 55, 3, 'Correct mais l''effet ne dure pas longtemps.', '2026-03-04 14:00:00'),
(7, 55, 4, 'Bon shampooing, parfum agréable.', '2026-03-06 16:00:00'),
(9, 55, 4, 'Mes cheveux sont plus faciles à coiffer.', '2026-03-08 10:00:00');

-- Lego City Pompiers (product 61) - 6 reviews, avg ~4.7
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 61, 5, 'Mon fils de 8 ans a adoré ! Montage facile et amusant.', '2026-03-01 10:00:00'),
(3, 61, 5, 'Excellent set Lego, très détaillé et de qualité.', '2026-03-02 14:00:00'),
(4, 61, 4, 'Beau set mais quelques pièces fragiles.', '2026-03-03 16:00:00'),
(5, 61, 5, 'Cadeau parfait pour un enfant. Il joue avec tous les jours.', '2026-03-04 11:00:00'),
(6, 61, 5, 'La caserne est superbe une fois montée.', '2026-03-05 13:00:00'),
(7, 61, 4, 'Très bien mais le prix est un peu élevé.', '2026-03-06 15:00:00');

-- Puzzle 1000 pièces (product 63) - 5 reviews, avg ~4.2
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 63, 5, 'Superbe puzzle, l''image est magnifique une fois terminé.', '2026-03-01 10:00:00'),
(4, 63, 4, 'Bonne qualité des pièces, elles s''emboîtent bien.', '2026-03-03 14:00:00'),
(6, 63, 4, 'Agréable moment en famille, difficulté bien dosée.', '2026-03-05 10:00:00'),
(8, 63, 4, 'Bon puzzle mais quelques pièces se ressemblent beaucoup.', '2026-03-07 16:00:00'),
(10, 63, 4, 'Parfait pour les soirées détente.', '2026-03-09 12:00:00');

-- Monopoly (product 64) - 7 reviews, avg ~4.1
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 64, 5, 'Le classique des jeux de société, toujours aussi fun !', '2026-03-01 20:00:00'),
(3, 64, 4, 'Parfait pour les soirées en famille.', '2026-03-02 21:00:00'),
(4, 64, 3, 'Les parties sont trop longues à mon goût.', '2026-03-03 20:30:00'),
(5, 64, 4, 'Bonne édition française, matériel de qualité.', '2026-03-04 19:00:00'),
(6, 64, 5, 'Indémodable ! Les enfants adorent.', '2026-03-05 20:00:00'),
(7, 64, 4, 'Toujours aussi drôle après des années.', '2026-03-06 21:00:00'),
(8, 64, 4, 'Un must-have pour les soirées jeux.', '2026-03-07 20:00:00');

-- Manette PS5 DualSense (product 41) - 5 reviews, avg ~4.4
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 41, 5, 'Le retour haptique est bluffant, on sent vraiment la différence.', '2026-03-01 10:00:00'),
(4, 41, 4, 'Très bonne manette mais l''autonomie pourrait être meilleure.', '2026-03-03 14:00:00'),
(6, 41, 5, 'La meilleure manette de jeu que j''ai utilisée.', '2026-03-05 10:00:00'),
(8, 41, 4, 'Prise en main agréable, gâchettes adaptatives géniales.', '2026-03-07 16:00:00'),
(10, 41, 4, 'Bon produit Sony, fiable et confortable.', '2026-03-09 12:00:00');

-- Enceinte JBL (product 40) - 4 reviews, avg ~4.5
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(3, 40, 5, 'Son puissant pour sa taille, basses profondes. Top !', '2026-03-02 10:00:00'),
(5, 40, 4, 'Bonne enceinte, waterproof et robuste.', '2026-03-04 14:00:00'),
(7, 40, 5, 'Parfaite pour les sorties et la plage.', '2026-03-06 16:00:00'),
(9, 40, 4, 'Autonomie impressionnante, 12h c''est pas du marketing.', '2026-03-08 10:00:00');

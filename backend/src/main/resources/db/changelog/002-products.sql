-- Alimentaire (category_id = 1)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('pasta-barilla-500g', 'Pâtes Barilla 500g', 'Pâtes italiennes de qualité supérieure', 1.89, 250, '/images/items/pasta-barilla-500g.jpg', 1, NULL),
('basmati-rice-1kg', 'Riz basmati 1kg', 'Riz basmati à grains longs, parfumé', 2.49, 200, '/images/items/basmati-rice-1kg.jpg', 1, NULL),
('olive-oil-1l', 'Huile d''olive vierge 1L', 'Huile d''olive extra vierge première pression à froid', 6.99, 180, '/images/items/olive-oil-1l.jpg', 1, NULL),
('butter-250g', 'Beurre doux Président 250g', 'Beurre doux de baratte Président, crémeux', 2.15, 220, '/images/items/butter-250g.jpg', 1, NULL),
('flour-t55-1kg', 'Farine de blé T55 1kg', 'Farine de blé tous usages', 1.09, 200, '/images/items/flour-t55-1kg.jpg', 1, NULL),
('sugar-1kg', 'Sucre en poudre 1kg', 'Sucre blanc en poudre', 1.29, 280, '/images/items/sugar-1kg.jpg', 1, NULL),
('guerande-salt-250g', 'Sel de Guérande 250g', 'Sel de mer récolté à la main', 3.49, 200, '/images/items/guerande-salt-250g.jpg', 1, NULL),
('organic-eggs-x12', 'Oeufs bio x12', 'Oeufs biologiques de poules élevées en plein air', 4.59, 180, '/images/items/organic-eggs-x12.jpg', 1, NULL);

-- Boissons (category_id = 2)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('evian-1-5l', 'Evian 1.5L', 'Eau minérale naturelle des Alpes', 0.89, 250, '/images/items/evian-1-5l.jpg', 2, NULL),
('perrier-1l', 'Perrier 1L', 'Eau minérale gazeuse naturelle', 1.19, 200, '/images/items/perrier-1l.jpg', 2, NULL),
('tropicana-orange-1l', 'Jus d''orange Tropicana 1L', 'Pur jus d''orange pressé, sans pulpe', 2.99, 150, '/images/items/tropicana-orange-1l.jpg', 2, NULL),
('coca-cola-1-5l', 'Coca-Cola 1.5L', 'Boisson gazeuse rafraîchissante', 1.79, 250, '/images/items/coca-cola-1-5l.jpg', 2, NULL),
('artisan-lemonade-75cl', 'Limonade artisanale 75cl', 'Limonade artisanale au citron de Menton', 3.29, 80, '/images/items/artisan-lemonade-75cl.jpg', 2, NULL),
('cristaline-x6', 'Eau de source Cristaline x6', 'Pack de 6 bouteilles d''eau de source 1.5L', 2.39, 200, '/images/items/cristaline-x6.jpg', 2, NULL),
('lipton-ice-tea-1-5l', 'Thé glacé Lipton 1.5L', 'Thé glacé saveur pêche', 1.99, 180, '/images/items/lipton-ice-tea-1-5l.jpg', 2, NULL);

-- Alcool (category_id = 3)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('bordeaux-rouge-2020', 'Vin rouge Bordeaux 2020 75cl', 'Vin rouge AOC Bordeaux, fruité et équilibré', 8.90, 80, '/images/items/bordeaux-rouge-2020.jpg', 3, NULL),
('cotes-du-rhone-2021', 'Vin rouge Côtes du Rhône 2021 75cl', 'Vin rouge AOC, notes épicées et fruitées', 6.50, 70, '/images/items/cotes-du-rhone-2021.jpg', 3, NULL),
('champagne-moet', 'Champagne Moët & Chandon 75cl', 'Champagne brut Impérial, bulles fines', 39.90, 30, '/images/items/champagne-moet.jpg', 3, NULL),
('leffe-blonde-75cl', 'Bière blonde Leffe 75cl', 'Bière d''abbaye blonde, douce et fruitée', 2.89, 120, '/images/items/leffe-blonde-75cl.jpg', 3, NULL),
('jameson-70cl', 'Whisky Jameson 70cl', 'Irish whiskey triple distillation, doux et rond', 22.90, 60, '/images/items/jameson-70cl.jpg', 3, NULL),
('rose-provence-2023', 'Vin rosé Provence 2023 75cl', 'Vin rosé AOP Côtes de Provence, frais et léger', 9.90, 90, '/images/items/rose-provence-2023.jpg', 3, NULL),
('bourgogne-pinot-noir-2019', 'Vin rouge Bourgogne Pinot Noir 2019 75cl', 'Vin rouge AOC Bourgogne, élégant et délicat', 12.50, 50, '/images/items/bourgogne-pinot-noir-2019.jpg', 3, NULL);

-- Vêtements (category_id = 4)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('black-leather-jacket', 'Veste en cuir noire', 'Veste en cuir véritable, coupe ajustée', 189.90, 50, '/images/items/black-leather-jacket.jpg', 4, NULL),
('slim-blue-jeans', 'Jean slim Levi''s bleu', 'Jean slim stretch Levi''s confortable, bleu délavé', 49.90, 50, '/images/items/slim-blue-jeans.jpg', 4, NULL),
('white-cotton-tshirt', 'T-shirt coton blanc', 'T-shirt 100% coton bio, coupe droite', 14.90, 50, '/images/items/white-cotton-tshirt.jpg', 4, NULL),
('grey-wool-sweater', 'Pull en laine gris', 'Pull en laine mérinos, chaud et doux', 69.90, 50, '/images/items/grey-wool-sweater.jpg', 4, NULL),
('linen-shirt', 'Chemise en lin', 'Chemise en lin naturel, idéale pour l''été', 59.90, 50, '/images/items/linen-shirt.jpg', 4, NULL),
('brown-leather-jacket', 'Blouson Schott cuir marron', 'Blouson Schott en cuir nubuck, style aviateur', 229.90, 50, '/images/items/brown-leather-jacket.jpg', 4, NULL),
('floral-summer-dress', 'Robe d''été fleurie', 'Robe légère à motifs floraux, fluide', 39.90, 50, '/images/items/floral-summer-dress.jpg', 4, NULL),
('winter-coat', 'Manteau d''hiver', 'Manteau long en laine mélangée, chaud et élégant', 149.90, 50, '/images/items/winter-coat.jpg', 4, NULL);

-- Multimédia (category_id = 5)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('macbook-pro-14', 'MacBook Pro 14"', 'Ordinateur portable Apple M3 Pro, 18Go RAM, 512Go SSD', 2399.00, 0, '/images/items/macbook-pro-14.jpg', 5, NULL),
('4k-monitor-27', 'Écran 27" 4K', 'Moniteur IPS 4K UHD, 60Hz, HDR400', 349.90, 35, '/images/items/4k-monitor-27.jpg', 5, NULL),
('mechanical-keyboard-rgb', 'Clavier mécanique RGB', 'Clavier mécanique switches Cherry MX, rétroéclairé', 89.90, 45, '/images/items/mechanical-keyboard-rgb.jpg', 5, NULL),
('wireless-mouse-logitech', 'Souris sans fil Logitech', 'Souris ergonomique sans fil, capteur 4000 DPI', 39.90, 60, '/images/items/wireless-mouse-logitech.jpg', 5, NULL),
('bluetooth-headphones-sony', 'Casque Bluetooth Sony', 'Casque sans fil à réduction de bruit active', 279.90, 30, '/images/items/bluetooth-headphones-sony.jpg', 5, NULL),
('webcam-hd-1080p', 'Webcam HD 1080p', 'Webcam Full HD avec microphone intégré', 49.90, 50, '/images/items/webcam-hd-1080p.jpg', 5, NULL),
('ssd-1tb', 'Disque dur SSD 1To', 'SSD NVMe M.2, vitesse lecture 3500 Mo/s', 79.90, 70, '/images/items/ssd-1tb.jpg', 5, NULL),
('bluetooth-speaker-jbl', 'Enceinte Bluetooth JBL', 'Enceinte portable waterproof, 12h d''autonomie', 99.90, 50, '/images/items/bluetooth-speaker-jbl.jpg', 5, NULL);

-- Jeux vidéo (category_id = 6)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('playstation-5', 'PlayStation 5', 'Console de jeu nouvelle génération, lecteur Blu-ray', 549.90, 0, '/images/items/playstation-5.jpg', 6, NULL),
('ps5-dualsense', 'Manette PS5 DualSense', 'Manette sans fil avec retour haptique', 69.90, 60, '/images/items/ps5-dualsense.jpg', 6, NULL),
('nintendo-switch-oled', 'Nintendo Switch OLED', 'Console hybride avec écran OLED 7 pouces', 349.90, 35, '/images/items/nintendo-switch-oled.jpg', 6, NULL),
('zelda-totk', 'The Legend of Zelda: Tears of the Kingdom - Switch', 'Aventure épique en monde ouvert sur Nintendo Switch', 59.90, 80, '/images/items/zelda-totk.jpg', 6, NULL),
('fifa-2026', 'EA FC 26 - PS5', 'Simulation de football, édition Coupe du Monde sur PlayStation 5', 69.90, 100, '/images/items/fifa-2026.jpg', 6, NULL),
('razer-gaming-headset', 'Casque gaming Razer', 'Casque gaming 7.1 surround, micro rétractable', 89.90, 45, '/images/items/razer-gaming-headset.jpg', 6, NULL),
('psn-card-50', 'Carte PSN 50€', 'Carte prépayée PlayStation Network 50€', 50.00, 200, '/images/items/psn-card-50.jpg', 6, NULL),
('xbox-game-pass-3m', 'Xbox Game Pass 3 mois', 'Abonnement Xbox Game Pass Ultimate 3 mois', 44.99, 0, '/images/items/xbox-game-pass-3m.jpg', 6, NULL);

-- Entretien (category_id = 7)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('skip-liquid-2l', 'Lessive liquide Skip 2L', 'Lessive liquide concentrée, 40 lavages', 9.99, 180, '/images/items/skip-liquid-2l.jpg', 7, NULL),
('ariel-pods-x30', 'Lessive pods Ariel x30', 'Capsules de lessive 3-en-1, fraîcheur alpine', 12.49, 150, '/images/items/ariel-pods-x30.jpg', 7, NULL),
('fairy-dish-soap-1l', 'Liquide vaisselle Fairy 1L', 'Liquide vaisselle ultra-dégraissant', 2.99, 200, '/images/items/fairy-dish-soap-1l.jpg', 7, NULL),
('sponges-x6', 'Éponges x6', 'Lot de 6 éponges grattantes multi-usages', 2.49, 250, '/images/items/sponges-x6.jpg', 7, NULL),
('lacroix-bleach-2l', 'Javel La Croix 2L', 'Eau de Javel désinfectante', 1.89, 200, '/images/items/lacroix-bleach-2l.jpg', 7, NULL),
('mr-clean-multi', 'Nettoyant multi-surfaces Mr Propre', 'Nettoyant ménager toutes surfaces, citron', 3.29, 180, '/images/items/mr-clean-multi.jpg', 7, NULL),
('trash-bags-50l-x20', 'Sacs poubelle 50L x20', 'Sacs poubelle résistants avec liens coulissants', 3.99, 220, '/images/items/trash-bags-50l-x20.jpg', 7, NULL);

-- Beauté & Hygiène (category_id = 8)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('elseve-shampoo-300ml', 'Shampooing Elsève 300ml', 'Shampooing réparateur pour cheveux abîmés', 3.99, 150, '/images/items/elseve-shampoo-300ml.jpg', 8, NULL),
('nivea-shower-gel-500ml', 'Gel douche Nivea 500ml', 'Gel douche hydratant à l''huile d''amande', 2.99, 180, '/images/items/nivea-shower-gel-500ml.jpg', 8, NULL),
('colgate-toothpaste-75ml', 'Dentifrice Colgate 75ml', 'Dentifrice blancheur avec fluor', 2.49, 200, '/images/items/colgate-toothpaste-75ml.jpg', 8, NULL),
('dove-deodorant-200ml', 'Déodorant Dove 200ml', 'Déodorant anti-transpirant 48h', 3.49, 170, '/images/items/dove-deodorant-200ml.jpg', 8, NULL),
('nivea-cream-200ml', 'Crème Nivea Soft 200ml', 'Crème hydratante légère et nourrissante pour le corps', 4.99, 140, '/images/items/nivea-cream-200ml.jpg', 8, NULL),
('gillette-razors-x4', 'Rasoirs Gillette x4', 'Pack de 4 rasoirs jetables triple lame', 6.99, 0, '/images/items/gillette-razors-x4.jpg', 8, NULL);

-- Jouets (category_id = 9)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('lego-city-fire', 'Lego City Pompiers', 'Set Lego City caserne de pompiers, 766 pièces', 59.90, 50, '/images/items/lego-city-fire.jpg', 9, NULL),
('barbie-fashionista', 'Poupée Barbie Fashionista', 'Poupée Barbie avec accessoires de mode', 14.90, 70, '/images/items/barbie-fashionista.jpg', 9, NULL),
('puzzle-1000', 'Puzzle 1000 pièces', 'Puzzle paysage de montagne, 1000 pièces', 12.90, 80, '/images/items/puzzle-1000.jpg', 9, NULL),
('monopoly-classic', 'Monopoly classique', 'Jeu de société classique, édition française', 24.90, 90, '/images/items/monopoly-classic.jpg', 9, NULL),
('plush-bear-40cm', 'Peluche ours 40cm', 'Peluche ours en peluche douce, lavable', 19.90, 60, '/images/items/plush-bear-40cm.jpg', 9, NULL),
('nerf-elite-2', 'Nerf Elite 2.0', 'Pistolet Nerf avec 12 fléchettes incluses', 29.90, 55, '/images/items/nerf-elite-2.jpg', 9, NULL),
('hot-wheels-track', 'Circuit Hot Wheels', 'Circuit de voitures avec looping et lanceur', 34.90, 65, '/images/items/hot-wheels-track.jpg', 9, NULL);

-- Jeux vidéo supplémentaires (category_id = 6)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('clair-obscur-expedition-33-ps5', 'Clair Obscur: Expedition 33 - PS5', 'RPG au tour par tour dans un monde onirique sur PlayStation 5', 69.90, 50, '/images/items/clair-obscur-expedition-33-ps5.jpg', 6, NULL),
('rdr2-ps4', 'Red Dead Redemption II - PS4', 'Aventure western en monde ouvert sur PlayStation 4', 29.90, 70, '/images/items/rdr2-ps4.jpg', 6, NULL),
('sekiro-xbox-one', 'Sekiro: Shadows Die Twice - Xbox One', 'Action-aventure exigeante dans le Japon féodal sur Xbox One', 39.90, 55, '/images/items/sekiro-xbox-one.jpg', 6, NULL),
('xbox-one-console', 'Console Xbox One', 'Console de jeu Microsoft Xbox One, 1To', 249.90, 25, '/images/items/xbox-one-console.jpg', 6, NULL),
('super-mario-3d-all-stars-switch', 'Super Mario 3D All-Stars - Switch', 'Compilation de 3 jeux Mario classiques en 3D sur Nintendo Switch', 49.90, 0, '/images/items/super-mario-3d-all-stars-switch.jpg', 6, NULL);

-- Produits supplémentaires

-- Alcool (category_id = 3)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('glenfiddich-12-70cl', 'Glenfiddich 12 Ans Speyside Single Malt 70cl', 'Single malt scotch whisky vieilli 12 ans, 40%', 34.90, 40, '/images/items/glenfiddich-12-70cl.jpg', 3, NULL),
('jack-daniels-70cl', 'Jack Daniel''s Old N°7 70cl', 'Tennessee whiskey charcoal mellowed, 40%', 24.90, 70, '/images/items/jack-daniels-70cl.jpg', 3, NULL),
('sancerre-blanc-2022', 'Vin blanc Sancerre 2022 75cl', 'Vin blanc AOC Sancerre, vif et minéral', 14.90, 60, '/images/items/sancerre-blanc-2022.jpg', 3, NULL),
('riesling-alsace-2021', 'Vin blanc Riesling d''Alsace 2021 75cl', 'Vin blanc AOC Alsace, sec et aromatique', 9.90, 55, '/images/items/riesling-alsace-2021.jpg', 3, NULL),
('chablis-2022', 'Vin blanc Chablis 2022 75cl', 'Vin blanc AOC Chablis, frais et minéral', 16.90, 45, '/images/items/chablis-2022.jpg', 3, NULL),
('haut-medoc-2019', 'Vin rouge Haut-Médoc 2019 75cl', 'Vin rouge AOC Haut-Médoc, structuré et élégant', 15.90, 50, '/images/items/haut-medoc-2019.jpg', 3, NULL);

-- Alimentaire (category_id = 1)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('viande-charal-steak', 'Steak haché Charal 15% MG x4', 'Steaks hachés pur bœuf français, 4x125g', 7.49, 120, '/images/items/viande-charal-steak.jpg', 1, NULL),
('poulet-fermier', 'Poulet fermier Loué Label Rouge', 'Poulet fermier Loué élevé en plein air, ~1.5kg', 12.90, 80, '/images/items/poulet-fermier.jpg', 1, NULL),
('comte-affine-12mois', 'Comté affiné 12 mois', 'Fromage Comté AOP, affiné en cave naturelle', 4.99, 100, '/images/items/comte-affine-12mois.jpg', 1, NULL),
('yaourt-fruits-danone-x4', 'Yaourt aux fruits Danone x4', 'Yaourts brassés aux fruits, 4x125g', 2.39, 150, '/images/items/yaourt-fruits-danone-x4.jpg', 1, NULL),
('yaourt-chocolat-danone-x4', 'Yaourt au chocolat Danone x4', 'Yaourts au chocolat onctueux, 4x125g', 2.49, 150, '/images/items/yaourt-chocolat-danone-x4.jpg', 1, NULL),
('nutella-750g', 'Nutella 750g', 'Pâte à tartiner aux noisettes et au cacao', 5.49, 200, '/images/items/nutella-750g.jpg', 1, NULL),
('kinder-bueno-x10', 'Kinder Bueno x10', 'Barres chocolatées fourrées au lait et noisettes', 4.99, 180, '/images/items/kinder-bueno-x10.jpg', 1, NULL);

-- Beauté & Hygiène (category_id = 8)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('brosse-dents-signal', 'Brosse à dents Signal Medium', 'Brosse à dents medium avec nettoyeur de langue', 2.49, 200, '/images/items/brosse-dents-signal.jpg', 8, NULL);

-- Entretien (category_id = 7)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('aspirateur-dyson-v15', 'Aspirateur Dyson V15 Detect', 'Aspirateur balai sans fil avec détection laser', 699.90, 15, '/images/items/aspirateur-dyson-v15.jpg', 7, NULL);

-- Jouets (category_id = 9)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('peluche-pikachu', 'Peluche Pokémon Pikachu 30cm', 'Peluche officielle Pokémon Pikachu, douce et câline', 24.90, 75, '/images/items/peluche-pikachu.jpg', 9, NULL);

-- Boissons supplémentaires (category_id = 2)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('fanta-orange-1-5l', 'Fanta Orange 1.5L', 'Boisson gazeuse à l''orange', 1.69, 200, '/images/items/fanta-orange-1-5l.jpg', 2, NULL),
('sprite-1-5l', 'Sprite 1.5L', 'Boisson gazeuse au citron et citron vert', 1.69, 200, '/images/items/sprite-1-5l.jpg', 2, NULL),
('oasis-tropical-1-5l', 'Oasis Tropical 1.5L', 'Boisson aux fruits tropicaux', 1.79, 180, '/images/items/oasis-tropical-1-5l.jpg', 2, NULL);

-- Jeu de société (category_id = 9)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('loup-garou', 'Les Loups-Garous de Thiercelieux', 'Jeu de société d''ambiance pour 8 à 18 joueurs', 12.90, 100, '/images/items/loup-garou.jpg', 9, '2001-01-01');

-- Jeux vidéo supplémentaires 2 (category_id = 6)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('mgsv-definitive-ps4', 'Metal Gear Solid V: The Definitive Experience - PS4', 'Édition complète incluant Ground Zeroes et The Phantom Pain sur PS4', 19.90, 60, '/images/items/mgsv-definitive-ps4.jpg', 6, '2016-10-11'),
('mgs-master-collection-ps5', 'Metal Gear Solid: Master Collection Vol.1 - PS5', 'Compilation MGS1 + MGS2 + MGS3 remasterisés sur PS5', 39.90, 40, '/images/items/mgs-master-collection-ps5.jpg', 6, '2023-10-24'),
('hitman-woa-ps5', 'Hitman: World of Assassination - PS5', 'Trilogie complète Hitman en un seul jeu sur PS5', 39.90, 45, '/images/items/hitman-woa-ps5.jpg', 6, '2023-01-26'),
('pokemon-lets-go-pikachu-switch', 'Pokémon: Let''s Go, Pikachu - Switch', 'Aventure Pokémon revisitée dans la région de Kanto sur Switch', 44.90, 35, '/images/items/pokemon-lets-go-pikachu-switch.jpg', 6, '2018-11-16');

-- Multimédia supplémentaires (category_id = 5)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('dell-xps-16', 'PC Portable Dell XPS 16 9640', 'Ordinateur portable Intel Core Ultra 7, 16Go RAM, 512Go SSD, écran 16" OLED', 1799.90, 20, '/images/items/dell-xps-16.jpg', 5, '2024-05-01'),
('iphone-17-orange', 'Apple iPhone 17 128Go Orange', 'Smartphone Apple iPhone 17, puce A19, écran 6.3" OLED, 128Go', 969.90, 25, '/images/items/iphone-17-orange.jpg', 5, '2025-09-19');

-- Livres - Informatique (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('clean-architecture', 'Clean Architecture - Robert C. Martin', 'Guide de conception logicielle et architecture propre', 39.90, 45, '/images/items/clean-architecture.jpg', 100, '2017-09-10'),
('domain-driven-design', 'Domain-Driven Design - Eric Evans', 'Approche de la complexité au cœur du logiciel', 49.90, 35, '/images/items/domain-driven-design.jpg', 100, '2003-08-22'),
('design-patterns-gof', 'Design Patterns - Gang of Four', 'Éléments de logiciels orientés objet réutilisables', 44.90, 30, '/images/items/design-patterns-gof.jpg', 100, '1994-10-31'),
('ocp-java-se-21', 'OCP Java SE 21 Developer - Exam 1Z0-830', 'Guide de préparation à la certification Oracle Java 21', 49.90, 40, '/images/items/ocp-java-se-21.jpg', 100, '2024-06-04');

-- Livres - Sport (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('guide-musculation-delavier', 'Guide des mouvements de musculation - Frédéric Delavier', 'Approche anatomique de chaque exercice de musculation', 24.90, 60, '/images/items/guide-musculation-delavier.jpg', 100, '2001-06-01'),
('methode-delavier-vol2', 'La méthode Delavier Vol.2 - Frédéric Delavier', 'Techniques avancées et programmes d''entraînement', 29.90, 50, '/images/items/methode-delavier-vol2.jpg', 100, '2010-06-01');

-- Livres - Comics (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('spiderman-integrale-t61', 'Spider-Man : L''intégrale (T61) - Marvel', 'Intégrale Spider-Man, tome 61', 29.00, 40, '/images/items/spiderman-integrale-t61.jpg', 100, '2023-10-04'),
('spiderman-integrale-t59', 'Spider-Man : L''intégrale 1990-1991 (T59) - Marvel', 'Intégrale Spider-Man 1990-1991', 29.00, 40, '/images/items/spiderman-integrale-t59.jpg', 100, '2023-03-01'),
('batman-killing-joke', 'Batman : Killing Joke - Alan Moore', 'Le chef-d''œuvre d''Alan Moore et Brian Bolland', 19.00, 55, '/images/items/batman-killing-joke.jpg', 100, '1988-03-01'),
('spiderman-jour-nouveau', 'Spider-Man : Un jour nouveau - Dan Slott', 'Nouveau départ pour Spider-Man par Dan Slott', 28.00, 35, '/images/items/spiderman-jour-nouveau.jpg', 100, '2008-01-01'),
('batman-long-halloween', 'Batman : Un Long Halloween - Jeph Loeb', 'Enquête épique de Batman par Jeph Loeb et Tim Sale', 28.00, 45, '/images/items/batman-long-halloween.jpg', 100, '1998-10-01');

-- Livres - Finance (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('investisseur-intelligent', 'L''investisseur intelligent - Benjamin Graham', 'Le guide classique de l''investissement en bourse', 25.90, 70, '/images/items/investisseur-intelligent.jpg', 100, '1949-01-01'),
('autoroute-millionnaire', 'L''autoroute du millionnaire - MJ DeMarco', 'La voie express vers la richesse', 24.90, 65, '/images/items/autoroute-millionnaire.jpg', 100, '2011-11-01'),
('pere-riche-pere-pauvre', 'Père riche, père pauvre - Robert Kiyosaki', 'Ce que les riches enseignent à leurs enfants', 10.90, 80, '/images/items/pere-riche-pere-pauvre.jpg', 100, '1997-04-01');

-- Livres - Romans (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('crime-orient-express', 'Le crime de l''Orient-Express - Agatha Christie', 'Le célèbre roman policier d''Hercule Poirot', 7.90, 120, '/images/items/crime-orient-express.jpg', 100, '1934-01-01'),
('1984-orwell', '1984 - George Orwell', 'Roman dystopique sur la surveillance et le totalitarisme', 8.90, 110, '/images/items/1984-orwell.jpg', 100, '1949-06-08'),
('sherlock-holmes-integrale', 'Sherlock Holmes : L''Intégrale illustrée - Arthur Conan Doyle', 'L''intégrale des aventures de Sherlock Holmes', 29.90, 50, '/images/items/sherlock-holmes-integrale.jpg', 100, '1892-01-01'),
('le-petit-prince', 'Le Petit Prince - Antoine de Saint-Exupéry', 'Conte philosophique et poétique universel', 8.50, 150, '/images/items/le-petit-prince.jpg', 100, '1943-04-06'),
('harry-potter-coffret', 'Harry Potter Coffret 7 livres - J.K. Rowling', 'L''intégrale des 7 tomes de la saga Harry Potter', 59.90, 40, '/images/items/harry-potter-coffret.jpg', 100, '2007-07-21');

-- Livres - Manga (category_id = 100)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('dbz-tome-01', 'Dragon Ball Z - 1re partie - Tome 01 : Les Saïyens - Akira Toriyama', 'Le début de la saga Dragon Ball Z', 6.90, 90, '/images/items/dbz-tome-01.jpg', 100, '1989-04-01'),
('dbz-tome-03-p3', 'Dragon Ball Z - 3e partie - Tome 03 : Le Super Saïyen/Freezer - Akira Toriyama', 'L''affrontement épique contre Freezer', 6.90, 75, '/images/items/dbz-tome-03-p3.jpg', 100, '1991-02-01'),
('naruto-pack-t1-t3', 'Pack Naruto Tomes 1 + 2 + 3 - Masashi Kishimoto', 'Les 3 premiers tomes de Naruto', 20.70, 60, '/images/items/naruto-pack-t1-t3.jpg', 100, '1999-09-01'),
('hunter-x-hunter-t1', 'Hunter X Hunter - Tome 1 - Yoshihiro Togashi', 'Le début de l''aventure de Gon Freecss', 6.90, 85, '/images/items/hunter-x-hunter-t1.jpg', 100, '1998-06-04'),
('one-piece-t01', 'One Piece Édition originale Tome 01 - Eiichiro Oda', 'À l''aube d''une grande aventure', 6.90, 100, '/images/items/one-piece-t01.jpg', 100, '1997-07-22'),
('gto-t1', 'GTO Tome 1 - Tōru Fujisawa', 'Great Teacher Onizuka, le prof le plus déjanté', 6.90, 70, '/images/items/gto-t1.jpg', 100, '1997-01-08');

-- Bières supplémentaires (category_id = 3)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('heineken-6x33cl', 'Heineken Bière blonde 5% 6x33cl', 'Pack de 6 bières blondes Heineken, 5% vol.', 5.99, 150, '/images/items/heineken-6x33cl.jpg', 3, NULL),
('hoegaarden-6x25cl', 'Hoegaarden Bière blanche 4.9° 6x25cl', 'Pack de 6 bières blanches Hoegaarden, 4.9% vol.', 6.49, 120, '/images/items/hoegaarden-6x25cl.jpg', 3, NULL);

-- Boissons supplémentaires (category_id = 2)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('red-bull-33cl', 'Red Bull Energy Drink 33cl', 'Boisson énergisante Red Bull, canette 33cl', 1.99, 200, '/images/items/red-bull-33cl.jpg', 2, NULL);

-- Alimentaire (category_id = 1)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('spaghetti-sauce-tomate', 'Spaghetti sauce tomate Panzani 400g', 'Spaghetti cuisinés à la sauce tomate', 1.89, 180, '/images/items/spaghetti-sauce-tomate.jpg', 1, NULL);

-- Jeux vidéo - Précommandes (category_id = 6)
INSERT INTO products (name, display_name, description, price, stock, image_url, category_id, release_date) VALUES
('gta-vi-ps5', 'Grand Theft Auto VI - PS5', 'Le prochain opus de la saga GTA par Rockstar Games sur PlayStation 5', 79.90, 50, '/images/items/gta-vi-ps5.jpg', 6, '2026-09-17'),
('007-first-light-ps5', '007 First Light - PS5', 'Jeu d''action-aventure James Bond par IO Interactive sur PlayStation 5', 69.90, 50, '/images/items/007-first-light-ps5.jpg', 6, '2026-12-01');

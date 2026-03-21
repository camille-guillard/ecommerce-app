-- Subcategories for existing categories
-- Current root categories: 1=food, 2=drinks, 3=alcohol, 4=clothing, 5=multimedia, 6=videogames, 7=household, 8=beauty, 9=toys

-- Alimentaire (1) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (10, 'grocery', 'Épicerie', 1);      -- id=10
INSERT INTO categories (id, name, display_name, parent_id) VALUES (11, 'fresh', 'Frais', 1);            -- id=11

-- Boissons (2) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (12, 'water', 'Eaux', 2);             -- id=12
INSERT INTO categories (id, name, display_name, parent_id) VALUES (13, 'soft-drinks', 'Jus & Sodas', 2);-- id=13

-- Alcool (3) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (14, 'wines', 'Vins', 3);             -- id=14
INSERT INTO categories (id, name, display_name, parent_id) VALUES (15, 'beers', 'Bières', 3);           -- id=15
INSERT INTO categories (id, name, display_name, parent_id) VALUES (16, 'spirits', 'Spiritueux', 3);     -- id=16

-- Mode (4 = clothing) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (17, 'men', 'Homme', 4);              -- id=17
INSERT INTO categories (id, name, display_name, parent_id) VALUES (18, 'women', 'Femme', 4);            -- id=18

-- Multimédia (5) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (19, 'computers', 'Ordinateurs', 5);  -- id=19
INSERT INTO categories (id, name, display_name, parent_id) VALUES (20, 'peripherals', 'Périphériques', 5);-- id=20
INSERT INTO categories (id, name, display_name, parent_id) VALUES (21, 'audio', 'Audio', 5);            -- id=21

-- Jeux vidéo (6) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (22, 'consoles', 'Consoles', 6);      -- id=22
INSERT INTO categories (id, name, display_name, parent_id) VALUES (23, 'games', 'Jeux', 6);             -- id=23
INSERT INTO categories (id, name, display_name, parent_id) VALUES (24, 'gaming-accessories', 'Accessoires', 6);-- id=24
INSERT INTO categories (id, name, display_name, parent_id) VALUES (25, 'cards-subs', 'Cartes & Abonnements', 6);-- id=25

-- Jouets (9) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (26, 'construction', 'Construction', 9);    -- id=26
INSERT INTO categories (id, name, display_name, parent_id) VALUES (27, 'board-games', 'Jeux de société', 9);  -- id=27
INSERT INTO categories (id, name, display_name, parent_id) VALUES (28, 'action-toys', 'Action & Peluches', 9);-- id=28

-- Multimédia (5) - Smartphones
INSERT INTO categories (id, name, display_name, parent_id) VALUES (29, 'smartphones', 'Smartphones', 5);  -- id=29

-- Livres (100) subcategories
INSERT INTO categories (id, name, display_name, parent_id) VALUES (101, 'it-books', 'Informatique', 100);      -- id=101
INSERT INTO categories (id, name, display_name, parent_id) VALUES (102, 'sport-books', 'Sport', 100);           -- id=102
INSERT INTO categories (id, name, display_name, parent_id) VALUES (103, 'comics', 'Comics', 100);               -- id=103
INSERT INTO categories (id, name, display_name, parent_id) VALUES (104, 'manga', 'Manga', 100);                 -- id=104
INSERT INTO categories (id, name, display_name, parent_id) VALUES (105, 'finance-books', 'Finance', 100);       -- id=105
INSERT INTO categories (id, name, display_name, parent_id) VALUES (106, 'novels', 'Romans', 100);               -- id=106

-- Entretien (7) and Beauté (8) have no subcategories

-- EN translations for subcategories
INSERT INTO category_translations (category_id, locale, display_name) VALUES (10, 'en', 'Grocery');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (11, 'en', 'Fresh');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (12, 'en', 'Water');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (13, 'en', 'Juices & Sodas');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (14, 'en', 'Wines');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (15, 'en', 'Beers');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (16, 'en', 'Spirits');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (17, 'en', 'Men');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (18, 'en', 'Women');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (19, 'en', 'Computers');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (20, 'en', 'Peripherals');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (21, 'en', 'Audio');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (22, 'en', 'Consoles');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (23, 'en', 'Games');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (24, 'en', 'Accessories');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (25, 'en', 'Cards & Subscriptions');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (26, 'en', 'Construction');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (27, 'en', 'Board Games');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (28, 'en', 'Action & Plush');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (29, 'en', 'Smartphones');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (100, 'en', 'Books');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (101, 'en', 'IT & Programming');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (102, 'en', 'Sports');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (103, 'en', 'Comics');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (104, 'en', 'Manga');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (105, 'en', 'Finance');
INSERT INTO category_translations (category_id, locale, display_name) VALUES (106, 'en', 'Novels');

-- Reassign products to subcategories
-- Alimentaire: Épicerie (10) = pâtes, riz, huile, farine, sucre, sel ; Frais (11) = beurre, oeufs
UPDATE products SET category_id = 10 WHERE name IN ('pasta-barilla-500g', 'basmati-rice-1kg', 'olive-oil-1l', 'flour-t55-1kg', 'sugar-1kg', 'guerande-salt-250g', 'nutella-750g', 'kinder-bueno-x10', 'spaghetti-sauce-tomate');
UPDATE products SET category_id = 11 WHERE name IN ('butter-250g', 'organic-eggs-x12', 'viande-charal-steak', 'poulet-fermier', 'comte-affine-12mois', 'yaourt-fruits-danone-x4', 'yaourt-chocolat-danone-x4');

-- Boissons: Eaux (12) = evian, perrier, cristaline ; Jus & Sodas (13) = tropicana, coca, limonade, lipton
UPDATE products SET category_id = 12 WHERE name IN ('evian-1-5l', 'perrier-1l', 'cristaline-x6');
UPDATE products SET category_id = 13 WHERE name IN ('tropicana-orange-1l', 'coca-cola-1-5l', 'artisan-lemonade-75cl', 'lipton-ice-tea-1-5l', 'fanta-orange-1-5l', 'sprite-1-5l', 'oasis-tropical-1-5l', 'red-bull-33cl');

-- Alcool: Vins (14) = bordeaux, côtes du rhône, rosé, bourgogne, champagne ; Bières (15) = leffe ; Spiritueux (16) = jameson
UPDATE products SET category_id = 14 WHERE name IN ('bordeaux-rouge-2020', 'cotes-du-rhone-2021', 'champagne-moet', 'rose-provence-2023', 'bourgogne-pinot-noir-2019', 'sancerre-blanc-2022', 'riesling-alsace-2021', 'chablis-2022', 'haut-medoc-2019');
UPDATE products SET category_id = 15 WHERE name IN ('leffe-blonde-75cl', 'heineken-6x33cl', 'hoegaarden-6x25cl');
UPDATE products SET category_id = 16 WHERE name IN ('jameson-70cl', 'glenfiddich-12-70cl', 'jack-daniels-70cl');

-- Mode: Homme (17) = all except robe ; Femme (18) = robe
UPDATE products SET category_id = 17 WHERE name IN ('black-leather-jacket', 'slim-blue-jeans', 'white-cotton-tshirt', 'grey-wool-sweater', 'linen-shirt', 'brown-leather-jacket', 'winter-coat');
UPDATE products SET category_id = 18 WHERE name IN ('floral-summer-dress');

-- Multimédia: Ordinateurs (19) = macbook ; Périphériques (20) = écran, clavier, souris, webcam, ssd ; Audio (21) = casque, enceinte
UPDATE products SET category_id = 19 WHERE name IN ('macbook-pro-14', 'dell-xps-16');
UPDATE products SET category_id = 29 WHERE name IN ('iphone-17-orange');
UPDATE products SET category_id = 20 WHERE name IN ('4k-monitor-27', 'mechanical-keyboard-rgb', 'wireless-mouse-logitech', 'webcam-hd-1080p', 'ssd-1tb');
UPDATE products SET category_id = 21 WHERE name IN ('bluetooth-headphones-sony', 'bluetooth-speaker-jbl');

-- Jeux vidéo: Consoles (22) = ps5, switch ; Jeux (23) = zelda, fifa ; Accessoires (24) = manette, casque razer ; Cartes (25) = psn, game pass
UPDATE products SET category_id = 22 WHERE name IN ('playstation-5', 'nintendo-switch-oled', 'xbox-one-console');
UPDATE products SET category_id = 23 WHERE name IN ('zelda-totk', 'fifa-2026', 'clair-obscur-expedition-33-ps5', 'rdr2-ps4', 'sekiro-xbox-one', 'super-mario-3d-all-stars-switch', 'mgsv-definitive-ps4', 'mgs-master-collection-ps5', 'hitman-woa-ps5', 'pokemon-lets-go-pikachu-switch', 'gta-vi-ps5', '007-first-light-ps5');
UPDATE products SET category_id = 24 WHERE name IN ('ps5-dualsense', 'razer-gaming-headset');
UPDATE products SET category_id = 25 WHERE name IN ('psn-card-50', 'xbox-game-pass-3m');

-- Jouets: Construction (26) = lego ; Jeux de société (27) = puzzle, monopoly ; Action (28) = nerf, hot wheels, barbie, peluche
UPDATE products SET category_id = 26 WHERE name IN ('lego-city-fire');
UPDATE products SET category_id = 27 WHERE name IN ('puzzle-1000', 'monopoly-classic', 'loup-garou');
UPDATE products SET category_id = 28 WHERE name IN ('barbie-fashionista', 'plush-bear-40cm', 'nerf-elite-2', 'hot-wheels-track', 'peluche-pikachu');

-- Livres subcategories
UPDATE products SET category_id = 101 WHERE name IN ('clean-architecture', 'domain-driven-design', 'design-patterns-gof', 'ocp-java-se-21');
UPDATE products SET category_id = 102 WHERE name IN ('guide-musculation-delavier', 'methode-delavier-vol2');
UPDATE products SET category_id = 103 WHERE name IN ('spiderman-integrale-t61', 'spiderman-integrale-t59', 'batman-killing-joke', 'spiderman-jour-nouveau', 'batman-long-halloween');
UPDATE products SET category_id = 104 WHERE name IN ('dbz-tome-01', 'dbz-tome-03-p3', 'naruto-pack-t1-t3', 'hunter-x-hunter-t1', 'one-piece-t01', 'gto-t1');
UPDATE products SET category_id = 105 WHERE name IN ('investisseur-intelligent', 'autoroute-millionnaire', 'pere-riche-pere-pauvre');
UPDATE products SET category_id = 106 WHERE name IN ('crime-orient-express', '1984-orwell', 'sherlock-holmes-integrale', 'le-petit-prince', 'harry-potter-coffret');

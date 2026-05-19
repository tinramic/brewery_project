-- Countries
CREATE TABLE IF NOT EXISTS country (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       name TEXT NOT NULL UNIQUE
);

-- Beer Styles
CREATE TABLE IF NOT EXISTS beer_style (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          name TEXT NOT NULL UNIQUE,
                                          description TEXT
);

-- Brewmasters
CREATE TABLE IF NOT EXISTS brewmaster (
                                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                                          name TEXT NOT NULL,
                                          bio TEXT,
                                          brewing_philosophy TEXT
);

-- Breweries
CREATE TABLE IF NOT EXISTS brewery (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       name TEXT NOT NULL,
                                       country_id INTEGER,
                                       FOREIGN KEY (country_id) REFERENCES country(id)
    );

-- Awards
CREATE TABLE IF NOT EXISTS award (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     name TEXT NOT NULL,
                                     year INTEGER,
                                     organization TEXT
);

-- Beers
CREATE TABLE IF NOT EXISTS beer (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name TEXT NOT NULL,
                                    abv REAL,
                                    ibu INTEGER,
                                    srm INTEGER,
                                    flavor_profile TEXT,
                                    image_path TEXT,
                                    brewery_id INTEGER,
                                    beer_style_id INTEGER,
                                    country_id INTEGER,
                                    brewmaster_id INTEGER,
                                    FOREIGN KEY (brewery_id) REFERENCES brewery(id),
    FOREIGN KEY (beer_style_id) REFERENCES beer_style(id),
    FOREIGN KEY (country_id) REFERENCES country(id),
    FOREIGN KEY (brewmaster_id) REFERENCES brewmaster(id)
    );

-- Beer Awards (veza između beera i nagrada, many-to-many)
CREATE TABLE IF NOT EXISTS beer_award (
                                          beer_id INTEGER,
                                          award_id INTEGER,
                                          PRIMARY KEY (beer_id, award_id),
    FOREIGN KEY (beer_id) REFERENCES beer(id),
    FOREIGN KEY (award_id) REFERENCES award(id)
    );

-- Users
CREATE TABLE IF NOT EXISTS user (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    username TEXT NOT NULL UNIQUE,
                                    password TEXT NOT NULL,
                                    role TEXT NOT NULL CHECK(role IN ('ADMINISTRATOR', 'USER'))
    );

-- Tasting List (degustacijska lista za Drag & Drop)
CREATE TABLE IF NOT EXISTS tasting_list (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            user_id INTEGER,
                                            name TEXT NOT NULL,
                                            FOREIGN KEY (user_id) REFERENCES user(id)
    );

-- Tasting List Items
CREATE TABLE IF NOT EXISTS tasting_list_item (
                                                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                                                 tasting_list_id INTEGER,
                                                 beer_id INTEGER,
                                                 position INTEGER,
                                                 FOREIGN KEY (tasting_list_id) REFERENCES tasting_list(id),
    FOREIGN KEY (beer_id) REFERENCES beer(id)
    );
-- =============================================
-- XOUND - Schema de Base de Datos (PostgreSQL)
-- =============================================

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    status BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Tabla de canciones
CREATE TABLE IF NOT EXISTS songs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    artist VARCHAR(200),
    tone VARCHAR(10),
    content TEXT,
    lyrics TEXT,
    notes TEXT,
    user_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_songs_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla de eventos
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    venue VARCHAR(200) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    share_code VARCHAR(20) UNIQUE,
    user_id INT NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabla pivote: setlist (relación evento <-> canción con orden)
CREATE TABLE IF NOT EXISTS setlist_songs (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL,
    song_id INT NOT NULL,
    position INT NOT NULL,
    CONSTRAINT fk_setlist_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_setlist_song FOREIGN KEY (song_id) REFERENCES songs(id),
    CONSTRAINT uq_setlist_event_song UNIQUE (event_id, song_id)
);

-- Datos iniciales: roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('MUSICIAN'), ('GUEST')
ON CONFLICT (name) DO NOTHING;

-- Usuario de prueba (password: Test1234)
INSERT INTO users (name, email, password, role_id)
VALUES ('Demo Musician', 'demo@xound.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 2)
ON CONFLICT (email) DO NOTHING;

-- Canciones de prueba
INSERT INTO songs (title, artist, tone, content, lyrics, notes, user_id) VALUES
('Bohemian Rhapsody', 'Queen', 'Bb', 'Intro - Verse - Chorus - Opera - Hard Rock - Outro', 'Is this the real life? Is this just fantasy?\nCaught in a landslide, no escape from reality\nOpen your eyes, look up to the skies and see...', 'Canción icónica, requiere buena coordinación en la sección ópera', 1),
('Hotel California', 'Eagles', 'Bm', 'Intro (guitarra acústica) - Verse - Chorus - Solo - Outro', 'On a dark desert highway, cool wind in my hair\nWarm smell of colitas, rising up through the air\nUp ahead in the distance, I saw a shimmering light...', 'Solo de guitarra largo al final, ensayar bien las armonías', 1),
('Wonderwall', 'Oasis', 'Em', 'Intro - Verse - Pre-Chorus - Chorus', 'Today is gonna be the day that they''re gonna throw it back to you\nBy now you should''ve somehow realized what you gotta do...', 'Rasgueo constante en acústica, buen tema para abrir shows', 1),
('Smells Like Teen Spirit', 'Nirvana', 'Fm', 'Intro (riff) - Verse - Pre-Chorus - Chorus - Solo', 'Load up on guns, bring your friends\nIt''s fun to lose and to pretend\nShe''s over-bored and self-assured...', 'Distorsión fuerte en el chorus, dinámica suave-fuerte', 1),
('Stairway to Heaven', 'Led Zeppelin', 'Am', 'Intro acústico - Verse - Build up - Solo - Finale', 'There''s a lady who''s sure all that glitters is gold\nAnd she''s buying a stairway to heaven...', 'Empieza suave y va creciendo, solo de guitarra épico', 1),
('Sweet Child O'' Mine', 'Guns N'' Roses', 'D', 'Intro (riff) - Verse - Chorus - Solo - Outro', 'She''s got a smile that it seems to me\nReminds me of childhood memories\nWhere everything was as fresh as the bright blue sky...', 'Riff de intro icónico, no olvidar el cambio de tempo al final', 1),
('Creep', 'Radiohead', 'G', 'Verse - Chorus - Verse - Chorus - Bridge - Chorus', 'When you were here before, couldn''t look you in the eye\nYou''re just like an angel, your skin makes me cry...', 'Golpe fuerte de guitarra antes del chorus, tema emotivo', 1),
('Come As You Are', 'Nirvana', 'Em', 'Intro (riff de bajo) - Verse - Chorus - Solo - Outro', 'Come as you are, as you were, as I want you to be\nAs a friend, as a friend, as an old enemy...', 'Efecto de chorus en la guitarra, bajo protagonista', 1),
('Knockin'' on Heaven''s Door', 'Bob Dylan', 'G', 'Verse - Chorus (repetir)', 'Mama, take this badge off of me\nI can''t use it anymore\nIt''s gettin'' dark, too dark to see...', 'Simple pero efectiva, buena para versión acústica o eléctrica', 1),
('Nothing Else Matters', 'Metallica', 'Em', 'Intro (fingerpicking) - Verse - Chorus - Solo - Outro', 'So close, no matter how far\nCouldn''t be much more from the heart\nForever trusting who we are...', 'Fingerpicking en intro, orquesta en la versión original', 1),
('Under the Bridge', 'Red Hot Chili Peppers', 'E', 'Intro - Verse - Chorus - Bridge - Outro con coros', 'Sometimes I feel like I don''t have a partner\nSometimes I feel like my only friend\nIs the city I live in, the city of angels...', 'Cambio de feeling entre verso y chorus, coros al final', 1),
('Zombie', 'The Cranberries', 'Em', 'Intro - Verse - Chorus - Verse - Chorus - Outro', 'Another head hangs lowly, child is slowly taken\nAnd the violence caused such silence, who are we mistaken...', 'Voz potente en el chorus, rasgueo agresivo', 1)
ON CONFLICT DO NOTHING;

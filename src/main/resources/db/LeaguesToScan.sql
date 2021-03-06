DROP TABLE IF EXISTS league_to_scan CASCADE;
CREATE TABLE IF NOT EXISTS league_to_scan (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(300) NOT NULL
);
INSERT INTO league_to_scan (id, name)
VALUES (default, 'Albania Superliga'),
       (default, 'Algeria Professional Ligue 1'),
       (default, 'Argentina Primera B Nacional'),
       (default, 'Argentina Superliga'),
       (default, 'AUSTRIA BUNDESLIGA'),
       (default, 'Australia Hyundai A League'),
       (default, 'Austria 2nd Liga'),
       (default, 'Azerbaijan Premier League'),
       (default, 'Bahrain Premier League'),
       (default, 'BELGIUM FIRST DIVISION A'),
       (default, 'Belgium First Division B'),
       (default, 'Bolivia Professional Football League'),
       (default, 'Chile Primera Division'),
       (default, 'Colombia Primera A'),
       (default, 'Colombia Primera B'),
       (default, 'Costa Rica Campeonato Primera Division'),
       (default, 'Croatia Prva Liga'),
       (default, 'Cyprus 1st Div'),
       (default, 'Czech Republic First League'),
       (default, 'DENMARK SUPER LEAGUE'),
       (default, 'Egypt Premier League'),
       (default, 'ENGLISH PREMIER LEAGUE'),
       (default, 'ENGLISH CHAMPIONSHIP'),
       (default, 'FRANCE DOMINO''S LIGUE 2'),
       (default, 'FRANCE LIGUE 1'),
       (default, 'GERMANY BUNDESLIGA'),
       (default, 'GERMANY BUNDESLIGA 2'),
       (default, 'Greece Football League'),
       (default, 'Greece Super League'),
       (default, 'Greece Super League 2'),
       (default, 'Guatemala Liga National'),
       (default, 'Hungary OTP Bank Liga NB 1'),
       (default, 'India I-League'),
       (default, 'India Super League'),
       (default, 'Iran Pro League'),
       (default, 'Israel Liga Leumit'),
       (default, 'Israel Premier League'),
       (default, 'ITALY SERIE A'),
       (default, 'ITALY SERIE B'),
       (default, 'Jamaica Premier League'),
       (default, 'Kenya Premier League'),
       (default, 'Kuwait Premier League'),
       (default, 'Malta BOV Premier League'),
       (default, 'Mexico Liga de Ascenso'),
       (default, 'Mexico Primera Division'),
       (default, 'Morocco Botola Pro'),
       (default, 'Myanmar National League'),
       (default, 'NETHERLANDS EREDIVISIE'),
       (default, 'Netherlands Eerste Divisie'),
       (default, 'Northern Ireland Danske Bank Premiership'),
       (default, 'Oman Professional League'),
       (default, 'Paraguay Primera Division'),
       (default, 'Peru Liga 1'),
       (default, 'PORTUGAL LIGA NOS'),
       (default, 'Poland Ekstraklasa'),
       (default, 'Portugal Ledman LigaPro'),
       (default, 'Romania Liga 1'),
       (default, 'Russia Premier League'),
       (default, 'Saudi Arabia Pro League'),
       (default, 'SCOTLAND PREMIERSHIP'),
       (default, 'Scotland Championship'),
       (default, 'South Africa ABSA Premiership'),
       (default, 'SPAIN LA LIGA'),
       (default, 'SPAIN LA LIGA 2'),
       (default, 'SWISS RAIFFEISEN SUPER LEAGUE'),
       (default, 'Swiss Challenge League'),
       (default, 'Tunisia Professional Ligue 1'),
       (default, 'TURKEY SUPER LEAGUE'),
       (default, 'Turkey 1st Lig'),
       (default, 'UAE Arabian Gulf League'),
       (default, 'Qatar QNB Stars League'),
       (default, 'Venezuela Primera Division'),
       (default, 'Wales Premier League Playoff');

INSERT INTO league_to_scan (id, name)
VALUES (default, 'New Zealand ISPS Handa Premiership'),
       (default, 'Bulgaria Second Professional Football League'),
       (default, 'Thailand League 1'),
       (default, 'Thailand League 2'),
       (default, 'Bulgaria First Professional Football League'),
       (default, 'Republic of North Macedonia Prva Liga'),
       (default, 'Slovakia Super League'),
       (default, 'Serbia Superliga'),
       (default, 'Ireland SSE Airtricity Premier Division'),
       (default, 'Uruguay Primera Division'),
       (default, 'Ecuador Liga Pro Serie A');

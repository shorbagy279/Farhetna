-- ============= V1__init.sql =============
-- FARHETNA Hall Booking Platform - Initial Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS farhetna_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE farhetna_db;

-- ============= Sample Data: Locations =============
INSERT INTO locations (name_ar, name_en, city_ar, city_en, region_ar, region_en, active, created_at, updated_at, deleted) 
VALUES 
('القاهرة', 'Cairo', 'القاهرة', 'Cairo', 'القاهرة', 'Cairo', true, NOW(), NOW(), false),
('الجيزة', 'Giza', 'الجيزة', 'Giza', 'الجيزة', 'Giza', true, NOW(), NOW(), false),
('الإسكندرية', 'Alexandria', 'الإسكندرية', 'Alexandria', 'الإسكندرية', 'Alexandria', true, NOW(), NOW(), false),
('المنصورة', 'Mansoura', 'المنصورة', 'Mansoura', 'الدقهلية', 'Dakahlia', true, NOW(), NOW(), false),
('طنطا', 'Tanta', 'طنطا', 'Tanta', 'الغربية', 'Gharbia', true, NOW(), NOW(), false),
('أسيوط', 'Asyut', 'أسيوط', 'Asyut', 'أسيوط', 'Asyut', true, NOW(), NOW(), false),
('الزقازيق', 'Zagazig', 'الزقازيق', 'Zagazig', 'الشرقية', 'Sharqia', true, NOW(), NOW(), false),
('المنيا', 'Minya', 'المنيا', 'Minya', 'المنيا', 'Minya', true, NOW(), NOW(), false);

-- ============= Sample Data: Amenities =============
INSERT INTO amenities (name_ar, name_en, description_ar, description_en, category, active, created_at, updated_at, deleted, icon_url)
VALUES
('موقف سيارات', 'Parking', 'موقف سيارات واسع', 'Spacious parking area', 'FACILITY', true, NOW(), NOW(), false, '/icons/parking.svg'),
('تكييف مركزي', 'Air Conditioning', 'تكييف مركزي حديث', 'Modern central AC', 'FACILITY', true, NOW(), NOW(), false, '/icons/ac.svg'),
('إنترنت واي فاي', 'WiFi', 'إنترنت عالي السرعة', 'High-speed internet', 'FACILITY', true, NOW(), NOW(), false, '/icons/wifi.svg'),
('شاشات عرض', 'Projectors', 'شاشات عرض احترافية', 'Professional display screens', 'EQUIPMENT', true, NOW(), NOW(), false, '/icons/projector.svg'),
('نظام صوتي', 'Sound System', 'نظام صوتي متطور', 'Advanced sound system', 'EQUIPMENT', true, NOW(), NOW(), false, '/icons/sound.svg'),
('إضاءة احترافية', 'Professional Lighting', 'إضاءة LED قابلة للتعديل', 'Adjustable LED lighting', 'EQUIPMENT', true, NOW(), NOW(), false, '/icons/lighting.svg'),
('خدمة ضيافة', 'Hospitality Service', 'فريق ضيافة محترف', 'Professional hospitality team', 'SERVICE', true, NOW(), NOW(), false, '/icons/service.svg'),
('أمن وحراسة', 'Security', 'أمن على مدار الساعة', '24/7 security service', 'SERVICE', true, NOW(), NOW(), false, '/icons/security.svg'),
('مسرح', 'Stage', 'مسرح احترافي', 'Professional stage', 'FACILITY', true, NOW(), NOW(), false, '/icons/stage.svg'),
('غرف تغيير', 'Changing Rooms', 'غرف تغيير للعروس والعريس', 'Bride and groom changing rooms', 'FACILITY', true, NOW(), NOW(), false, '/icons/room.svg'),
('منطقة أطفال', 'Kids Area', 'منطقة لعب للأطفال', 'Children play area', 'FACILITY', true, NOW(), NOW(), false, '/icons/kids.svg'),
('مدخل منفصل للنساء', 'Ladies Entrance', 'مدخل خاص للسيدات', 'Separate ladies entrance', 'ACCESSIBILITY', true, NOW(), NOW(), false, '/icons/ladies.svg'),
('مصعد', 'Elevator', 'مصعد كهربائي', 'Electric elevator', 'ACCESSIBILITY', true, NOW(), NOW(), false, '/icons/elevator.svg'),
('وصول لذوي الاحتياجات', 'Wheelchair Access', 'مناسب لذوي الاحتياجات الخاصة', 'Wheelchair accessible', 'ACCESSIBILITY', true, NOW(), NOW(), false, '/icons/wheelchair.svg');

-- ============= Sample Data: Admin User =============
-- Password: admin123 (BCrypt hashed)
INSERT INTO users (email, password, full_name, phone_number, role, preferred_language, active, email_verified, phone_verified, created_at, updated_at, deleted, profile_image_url)
VALUES 
('admin@farhetna.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 
 'System Administrator', '+201000000000', 'ADMINISTRATOR', 'ARABIC', true, true, true, 
 NOW(), NOW(), false, null);

SET @admin_user_id = LAST_INSERT_ID();

INSERT INTO administrators (user_id, admin_role, created_at, updated_at, deleted, last_login_at)
VALUES (@admin_user_id, 'SUPER_ADMIN', NOW(), NOW(), false, NOW());

-- ============= Sample Data: Test Hall Owner =============
-- Password: owner123
INSERT INTO users (email, password, full_name, phone_number, role, preferred_language, active, email_verified, phone_verified, created_at, updated_at, deleted)
VALUES 
('owner@farhetna.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 
 'Ahmed Mohamed', '+201111111111', 'HALL_OWNER', 'ARABIC', true, true, true, 
 NOW(), NOW(), false);

SET @owner_user_id = LAST_INSERT_ID();

INSERT INTO hall_owners (user_id, business_name, business_license, tax_id, verified, created_at, updated_at, deleted, verified_at)
VALUES (@owner_user_id, 'Golden Events', 'BL-2024-001', 'TX-123456', true, NOW(), NOW(), false, NOW());

SET @owner_id = LAST_INSERT_ID();

-- ============= Sample Data: Test Customer =============
-- Password: customer123
INSERT INTO users (email, password, full_name, phone_number, role, preferred_language, active, email_verified, phone_verified, created_at, updated_at, deleted)
VALUES 
('customer@farhetna.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 
 'Sara Ali', '+201222222222', 'CUSTOMER', 'ARABIC', true, true, true, 
 NOW(), NOW(), false);

SET @customer_user_id = LAST_INSERT_ID();

INSERT INTO customers (user_id, total_bookings, completed_bookings, created_at, updated_at, deleted)
VALUES (@customer_user_id, 0, 0, NOW(), NOW(), false);

-- ============= Sample Data: Test Hall =============
INSERT INTO halls (owner_id, location_id, name_ar, name_en, description_ar, description_en, 
                   capacity, address, latitude, longitude, active, verified, average_rating, 
                   total_ratings, starting_price, created_at, updated_at, deleted)
VALUES 
(@owner_id, 1, 'قاعة النيل الذهبية', 'Golden Nile Hall', 
 'قاعة فاخرة مطلة على النيل مع إطلالة ساحرة', 
 'Luxury hall overlooking the Nile with stunning view',
 500, 'شارع الكورنيش، المعادي، القاهرة', 29.9600, 31.2800, 
 true, true, 4.5, 25, 15000.00, NOW(), NOW(), false);

SET @hall_id = LAST_INSERT_ID();

-- ============= Sample Data: Hall Images =============
INSERT INTO hall_images (hall_id, image_url, display_order, is_primary, created_at, updated_at, deleted)
VALUES 
(@hall_id, '/images/halls/hall1-main.jpg', 0, true, NOW(), NOW(), false),
(@hall_id, '/images/halls/hall1-interior.jpg', 1, false, NOW(), NOW(), false),
(@hall_id, '/images/halls/hall1-stage.jpg', 2, false, NOW(), NOW(), false),
(@hall_id, '/images/halls/hall1-exterior.jpg', 3, false, NOW(), NOW(), false);

-- ============= Sample Data: Hall Amenities =============
INSERT INTO hall_amenities (hall_id, amenity_id)
VALUES 
(@hall_id, 1), -- Parking
(@hall_id, 2), -- AC
(@hall_id, 3), -- WiFi
(@hall_id, 4), -- Projectors
(@hall_id, 5), -- Sound System
(@hall_id, 6), -- Lighting
(@hall_id, 7), -- Hospitality
(@hall_id, 8), -- Security
(@hall_id, 9); -- Stage

-- ============= Sample Data: Packages =============
INSERT INTO packages (hall_id, name_ar, name_en, description_ar, description_en, type, price, active, created_at, updated_at, deleted)
VALUES 
(@hall_id, 'باقة أساسية', 'Basic Package', 'باقة مناسبة للمناسبات الصغيرة', 
 'Suitable for small events', 'BASIC', 15000.00, true, NOW(), NOW(), false),
(@hall_id, 'باقة بريميوم', 'Premium Package', 'باقة متكاملة للحفلات المتوسطة', 
 'Complete package for medium events', 'PREMIUM', 25000.00, true, NOW(), NOW(), false),
(@hall_id, 'باقة فاخرة', 'Luxury Package', 'باقة حصرية للحفلات الكبرى', 
 'Exclusive package for grand events', 'LUXURY', 40000.00, true, NOW(), NOW(), false);

SET @basic_package_id = LAST_INSERT_ID();

-- ============= Sample Data: Package Inclusions =============
INSERT INTO package_inclusions (package_id, item_ar, item_en)
VALUES 
(@basic_package_id, 'استخدام القاعة لمدة 6 ساعات', 'Hall use for 6 hours'),
(@basic_package_id, 'إضاءة أساسية', 'Basic lighting'),
(@basic_package_id, 'نظام صوتي', 'Sound system'),
(@basic_package_id, 'كراسي وطاولات', 'Chairs and tables'),
(@basic_package_id, 'خدمة أمن', 'Security service');

-- ============= Sample Data: Add-Ons =============
INSERT INTO add_ons (hall_id, name_ar, name_en, description_ar, description_en, category, price, active, created_at, updated_at, deleted)
VALUES 
(@hall_id, 'بوفيه مفتوح', 'Open Buffet', 'بوفيه مفتوح لـ 200 شخص', 
 'Open buffet for 200 people', 'CATERING', 8000.00, true, NOW(), NOW(), false),
(@hall_id, 'تصوير فوتوغرافي', 'Photography', 'مصور محترف طوال الحفل', 
 'Professional photographer', 'PHOTOGRAPHY', 3000.00, true, NOW(), NOW(), false),
(@hall_id, 'تصوير فيديو', 'Videography', 'تصوير فيديو احترافي', 
 'Professional videography', 'PHOTOGRAPHY', 5000.00, true, NOW(), NOW(), false),
(@hall_id, 'ديكور زهور', 'Flower Decoration', 'تنسيق زهور فاخر', 
 'Luxury flower arrangement', 'DECORATION', 4000.00, true, NOW(), NOW(), false),
(@hall_id, 'فرقة موسيقية', 'Live Band', 'فرقة موسيقية حية', 
 'Live music band', 'ENTERTAINMENT', 6000.00, true, NOW(), NOW(), false),
(@hall_id, 'كوشة عرائس', 'Wedding Stage', 'كوشة عرائس فاخرة', 
 'Luxury wedding stage', 'DECORATION', 7000.00, true, NOW(), NOW(), false);

-- ============= Indexes for Performance =============
CREATE INDEX idx_bookings_customer ON bookings(customer_id, status);
CREATE INDEX idx_bookings_hall ON bookings(hall_id, event_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_blocked_dates_hall_date ON blocked_dates(hall_id, date);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_halls_location ON halls(location_id);
CREATE INDEX idx_halls_active_verified ON halls(active, verified);

-- ============= Sample Announcement =============
INSERT INTO announcements (created_by_admin_id, title_ar, title_en, content_ar, content_en, 
                          target_audience, active, published_at, send_notification, priority, 
                          created_at, updated_at, deleted)
VALUES 
(@admin_user_id, 
 'مرحباً بك في فرحتنا', 
 'Welcome to Farhetna',
 'نحن سعداء بانضمامك إلى منصة فرحتنا - أول منصة لحجز قاعات الأفراح في مصر',
 'We are happy to have you on Farhetna platform - the first wedding halls booking platform in Egypt',
 'ALL_USERS', true, NOW(), true, 'HIGH', NOW(), NOW(), false);

COMMIT;
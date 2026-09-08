package com.example.data

object QuestData {

  val steps: List<QuestStep> = listOf(
    QuestStep(
      id = 1,
      stepNumberText = "[1]",
      title = "อยู่รอดใน Her House",
      area = "Her House",
      visualCue = "บ้านเริ่มต้นเกิดเกม",
      routeFrom = "จุดเกิด (Spawn)",
      instructions = listOf(
        "อยู่รอดและเอาชีวิตรอดในบริเวณบ้าน",
        "รอจนกระทั่งได้ยินเสียงโทรศัพท์ดังขึ้นและเปิดให้ใช้งาน"
      ),
      onCompleted = "โทรศัพท์ภายในบ้านเริ่มส่งเสียงดัง",
      emergencySwarm = "วิ่งวนรอบพื้นที่บ้าน อย่าขังตัวเองหรือจนมุมในห้องแคบเด็ดขาด",
      checkBeforeNext = "โทรศัพท์ดังแล้ว พร้อมกดรับสาย",
      note = null,
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 2,
      stepNumberText = "[2]",
      title = "รับโทรศัพท์",
      area = "Her House",
      visualCue = "เครื่องโทรศัพท์ภายในตัวบ้าน",
      routeFrom = "เดินกลับเข้าด้านในบ้าน",
      instructions = listOf(
        "เดินไปที่เครื่องโทรศัพท์",
        "กด Interact เพื่อรับโทรศัพท์และฟังข้อความ"
      ),
      onCompleted = "เส้นทางออกจากบ้านปลดล็อก และ Portal เปิดทำงาน",
      emergencySwarm = "ล่อให้เหลือซอมบี้ช้าๆ ไว้ 1 ตัว แล้วค่อยเดินเข้าไปรับโทรศัพท์อย่างปลอดภัย",
      checkBeforeNext = "Portal ด้านนอกบ้านเริ่มทำงานและส่องแสง",
      note = null,
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 3,
      stepNumberText = "[3]",
      title = "เข้า Portal สู่ Nexus Forge",
      area = "นอก Her House",
      visualCue = "Portal ริมหน้าผา",
      routeFrom = "ออกจากตัวบ้านเดินไปที่หน้าผา",
      instructions = listOf(
        "เดินไปที่ขอบหน้าผา",
        "ก้าวเข้าสู่ Portal เพื่อเทเลพอร์ตไปยัง Nexus Forge"
      ),
      onCompleted = "เดินทางมาถึง Nexus Forge (ฮับศูนย์กลาง)",
      emergencySwarm = "วิ่งตรงเข้า Portal ทันที อย่าหยุดยิงกลางทาง",
      checkBeforeNext = "มองเห็นแท่นบูชากลาง Nexus Forge",
      note = "Nexus Forge คือจุดศูนย์กลาง ถ้าหลงทางให้กลับมาที่นี่ก่อนเสมอ",
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 4,
      stepNumberText = "[4]",
      title = "วาง World Seed",
      area = "Nexus Forge",
      visualCue = "แท่นบูชากลาง Nexus Forge",
      routeFrom = "เดินจากจุดวาร์ป Portal เข้าสู่แท่นกลาง",
      instructions = listOf(
        "นำ World Seed ไปวางที่แท่นบูชากลาง",
        "กด Interact เพื่อเริ่มต้นพิธีกรรม Dread Skull"
      ),
      onCompleted = "Dread Skull ปรากฏตัวและเริ่มบินหนีไปยังวิหาร",
      emergencySwarm = "เคลียร์ซอมบี้รอบแท่นกลางให้โล่งก่อนเข้าไปกดใช้งาน",
      checkBeforeNext = "เห็นกะโหลกลอย Dread Skull พุ่งหนีออกไป",
      note = null,
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 5,
      stepNumberText = "[5]",
      title = "ไล่ล่า Dread Skull ตัวแรก",
      area = "Nexus → วิหารที่ Skull สุ่มเลือก",
      visualCue = "กะโหลกลอยเปล่งแสง (Dread Skull)",
      routeFrom = "วิ่งตามทิศทางที่ Dread Skull บินไป",
      instructions = listOf(
        "วิ่งไล่ตาม Dread Skull อย่างต่อเนื่อง",
        "เปิดสิ่งกีดขวางตามทางที่ Skull พาไป",
        "ระดมยิงทำลาย Skull เมื่อมันหยุดนิ่ง"
      ),
      onCompleted = "เปิดเส้นทางมุ่งสู่วิหาร Usurped Flame",
      emergencySwarm = "อย่าละสายตาจาก Dread Skull วิ่งตามเป็นหลัก ซอมบี้ตามหลังให้วิ่งวนลาก",
      checkBeforeNext = "Dread Skull ถูกทำลายเรียบร้อย",
      note = "สำคัญมาก: อย่าจำชื่อวิหารตายตัว ให้จับตาดูและตาม Skull ในเกมของตัวเอง!",
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 6,
      stepNumberText = "[6]",
      title = "เก็บ Usurped Flame",
      area = "Main Chamber ของวิหารที่ Skull พาไป",
      visualCue = "กระถางไฟโบราณ (Brazier)",
      routeFrom = "จากตำแหน่งที่ทำลาย Skull เข้าสู่ห้องโถงหลัก",
      instructions = listOf(
        "เข้าหากระถางไฟในห้องโถงหลัก",
        "กด Interact เก็บ Usurped Flame พกพากลับ Nexus"
      ),
      onCompleted = "ตัวละครถือดวงไฟ Usurped Flame พร้อมนำกลับ",
      emergencySwarm = "เคลียร์เส้นทางทางออกก่อนกดหยิบไฟ เพื่อไม่ให้โดนดักหน้าประตู",
      checkBeforeNext = "ไฟ Usurped Flame อยู่ในมือ/ตัวผู้เล่น",
      note = null,
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 7,
      stepNumberText = "[7]",
      title = "จุดกระถางไฟ 2 จุด (Nexus Forge)",
      area = "Nexus Forge",
      visualCue = "Brazier สองข้างแท่นบูชากลาง",
      routeFrom = "เดินทางกลับจากวิหารสู่ Nexus Forge",
      instructions = listOf(
        "นำ Usurped Flame ไปใส่ใน Brazier ฝั่งแรก",
        "ทำตาม Dread Skull ตัวที่ 2 ไปเก็บ Flame อีกจุด",
        "นำไฟก้อนที่ 2 มาใส่ Brazier อีกฝั่งให้ครบ"
      ),
      onCompleted = "Brazier สองข้างแท่นกลางติดไฟเปลวเพลิงลุกโชนครบทั้งคู่",
      emergencySwarm = "ทำทีละอันอย่างใจเย็น วิ่งวนลากซอมบี้รอบแท่นกลางกว้างๆ",
      checkBeforeNext = "เปลวไฟติดสว่างสมบูรณ์ทั้งสองฝั่ง",
      note = null,
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 8,
      stepNumberText = "[8]",
      title = "เปิดเครื่อง Pack-a-Punch (PaP)",
      area = "Nexus Forge",
      visualCue = "คันโยกหมุน (Crank) สองข้างแท่นกลาง",
      routeFrom = "ยืนอยู่บริเวณรอบแท่นกลาง Nexus",
      instructions = listOf(
        "หมุนคันโยก Crank ฝั่งแรก",
        "วิ่งวนเคลียร์รอบๆ แล้วหมุน Crank อีกฝั่ง",
        "รอให้กลไกแท่นกลางเลื่อนลดระดับลง"
      ),
      onCompleted = "แท่นเลื่อนลง ปลดล็อกเครื่อง Pack-a-Punch ให้ใช้งานได้ทันที",
      emergencySwarm = "หมุนทีละฝั่ง อย่ากดแช่ถ้าซอมบี้ประชิด วิ่งวนลูปก่อนหมุนต่อ",
      checkBeforeNext = "ตู้ Pack-a-Punch มีแสงพร้อมอัปเกรดปืน",
      note = "แนะนำให้อัปเกรดปืนหลักเป็น PaP I ทันทีที่ทำได้เพื่อความง่ายในขั้นถัดไป",
      category = StepCategory.EARLY
    ),
    QuestStep(
      id = 9,
      stepNumberText = "[9]",
      title = "เก็บสลิงดึง Void Claw",
      area = "รอบ Nexus Forge",
      visualCue = "เสา/โคมที่มีลูกแก้วเรืองแสงสีเขียว",
      routeFrom = "เดินสำรวจรอบๆ แท่น Pack-a-Punch",
      instructions = listOf(
        "มองหาเสาหรือโคมไฟที่มีลูกแก้วพลังงานสีเขียว",
        "กด Interact เพื่อหยิบสลิง Void Claw ติดตัว"
      ),
      onCompleted = "ได้รับสลิง Grapple (Void Claw) ใช้งานในช่อง Tactical",
      emergencySwarm = "กดหยิบแล้วสลิงหนีขึ้นที่สูงหรือเคลื่อนที่ข้ามฝั่งทันที",
      checkBeforeNext = "ตรวจสอบช่อง Tactical Equipment ว่าเป็น Void Claw แล้ว",
      note = "Void Claw เป็น Tactical สลิงดึง ไม่ใช่อาวุธมหัศจรรย์ (Wonder Weapon)",
      category = StepCategory.WEAPON
    ),
    QuestStep(
      id = 10,
      stepNumberText = "[10]",
      title = "สร้าง Wonder Weapon: Warden's Blight (10A - 10H)",
      area = "Dravakar & Nyxara & Nexus",
      visualCue = "Fracture, Astral Flame, Whispers, ทั่ง Anvil",
      routeFrom = "ปฏิบัติตามลำดับย่อย 10A ถึง 10H",
      instructions = listOf(
        "10A: Dravakar Entrance - ใช้ Void Claw ขึ้นชั้นสอง ระเบิดผนังขวา เก็บ Fracture of Nyxara",
        "10B: Nyxara Main Chamber - วาง Fracture บนแท่นเหนือกล่องกระสุน",
        "10C: Nexus Forge - Void Claw ดึง Purple Orb เข้ากลาง แล้วกด Surge Nexus เก็บ Astral Flame",
        "10D: Nyxara Sanctuary - ยิง Astral Flame ใส่ Eye แล้วหมุน Crystal 3 จุดให้ลำแสงเชื่อมกัน เปิด Inner Sanctum",
        "10E: Nyxara Inner Sanctum - กด Orb พื้นถล่ม ให้เถาวัลย์ Vine ดูดพลังจนเต็ม แล้วกินผล Fruit (Fruit ไม่เกิดก่อน Round 8)",
        "10F: Whispers of the Past - เก็บ 3 ชิ้นจริง: Bow (ข้างศพในถ้ำ) + Stock (ข้างศพพิงหินกลาง) + Handles (ใต้พื้นบ้านพัง)",
        "10G: Dravakar Sanctuary - ฟัง/อ่านข้อความสีน้ำเงิน กดสวิตช์ผนัง แล้วยืนยันที่เสากลาง (สุ่มทุกเกม)",
        "10H: Dravakar Inner Sanctum - Void Claw ขึ้นช่องบน กด Anvil เพื่อสร้าง Warden's Blight"
      ),
      onCompleted = "ได้รับสุดยอดอาวุธมหัศจรรย์ Warden's Blight!",
      emergencySwarm = "ใน Whispers of the Past วิ่งวนกลางป่าก่อน แล้วค่อยแวะหยิบชิ้นส่วน",
      checkBeforeNext = "ถือ Warden's Blight ในมือ และนำไป Pack-a-Punch เพื่อเปิดใช้ Charged Shot",
      note = "ข้อควรระวัง: ชิ้นส่วนจริงคือ Bow + Stock + Handles (ไม่ใช่ Hammer, Scroll, Thread)",
      category = StepCategory.WEAPON
    ),
    QuestStep(
      id = 11,
      stepNumberText = "[11]",
      title = "อัปเกรดสลิง Void Claw → Void Talon (11A - 11E)",
      area = "Nexus Forge & Her House",
      visualCue = "ลูกบาสเกตบอล, สัญลักษณ์น้ำเงิน 4 จุด, Exfil, Eye of the Forge",
      routeFrom = "เริ่มต้นที่ Nexus Forge แล้วกลับ Her House",
      instructions = listOf(
        "11A: Nexus Forge - ยิงลูกบาสเกตบอลบนซากหลังคา Her House ให้ตกลงมา",
        "11B: Her House - สำรวจและจดจำสัญลักษณ์สีน้ำเงิน 4 จุด เรียงลำดับ 1 → 2 → 3 → 4 (สุ่มทุกเกม)",
        "11C: Exfil Round - ยิงสัญลักษณ์ 1→2→3→4 ตามลำดับ กดเรียก Exfil ฆ่ามอน HVT แล้วเข้า Portal กลับบ้าน",
        "11D: Her House - ใช้ Charged Warden's Blight ยิงเข้าหน้าต่างตรงข้าม Coda-9 เก็บ Eye of the Forge ฆ่า Doppelghast + Ravager",
        "11E: Nexus Forge - นำ Eye of the Forge ไปใส่ในเสา Void Claw สีเขียวที่ต้องการ"
      ),
      onCompleted = "เสานั้นจะอัปเกรดเป็น Void Talon ที่ทรงพลังยิ่งขึ้น",
      emergencySwarm = "ตอนเกิด Lockdown ใน Her House ยิง Charged Blight ควบคุมฝูงและฆ่ามอนพิเศษด่วน",
      checkBeforeNext = "ได้รับ Void Talon ติดตั้งในช่อง Tactical เรียบร้อย",
      note = "สำคัญ: เฉพาะเสาที่นำ Eye of the Forge ไปใส่เท่านั้นที่จะเปลี่ยนเป็น Void Talon",
      category = StepCategory.WEAPON
    ),
    QuestStep(
      id = 12,
      stepNumberText = "[12]",
      title = "ซ่อมแซมกลไก Nexus Forge",
      area = "Nexus Forge",
      visualCue = "ผนึก Seal บนเสา 3 จุด และห้องใต้ PaP",
      routeFrom = "Nexus Forge รอบแท่นกลางและชั้นใต้ดิน",
      instructions = listOf(
        "1. ใช้ Void Talon ทำลายผนึก Seal บนเสา 3 จุดรอบ Nexus",
        "2. ลงไปห้องใต้ดินใต้เครื่อง Pack-a-Punch",
        "3. ใช้ Void Talon ทำลาย Seal บนผนัง",
        "4. ใช้ระเบิดทำลายใยแมงมุมที่เกาะติดฟันเฟืองเครื่องจักร",
        "5. กลับขึ้นมาด้านบน กด Surge Nexus",
        "6. ลงล่างอีกครั้ง ดึงคันโยก Lever ค้างไว้จนกระทั่งล็อกแน่น"
      ),
      onCompleted = "กลไก Nexus Forge ได้รับการซ่อมแซมและสามารถหมุนไปยังวิหารต่างๆ ได้",
      emergencySwarm = "ทำลาย Seal ทีละจุด แล้ววิ่งเคลียร์ อย่ามุดลงชั้นใต้ดินถ้ามีซอมบี้กวดติดหลัง",
      checkBeforeNext = "กลไกเฟือง Forge หมุนได้อิสระ ไม่มีใยแมงมุมติดขัด",
      note = null,
      category = StepCategory.FORGE_CUBE
    ),
    QuestStep(
      id = 13,
      stepNumberText = "[13]",
      title = "แก้ปริศนาลูกบาศก์ Veytharion Cube Puzzle",
      area = "Veytharion Sanctuary",
      visualCue = "Cube 4 ก้อน (Fire, Hand, Water, Flower)",
      routeFrom = "จาก Nexus Forge มุ่งหน้าสู่วิหาร Veytharion",
      instructions = listOf(
        "เข้าสู่วิหาร Veytharion จะพบลูบาศก์ 4 ชนิด: Fire, Hand, Water, Flower",
        "กฎเหล็ก: ห้ามให้ Fire สัมผัสกับ Water หรือ Flower เด็ดขาด!",
        "ใช้อัลกอริทึมแก้ลำดับ (ดูในแท็บพัซเซิล):",
        "F H W Fl → F-H → H-H → สลับขวา → F-R → สลับซ้าย → W-H → สลับขวา → H-R → W-R → F-H → H-H → สลับซ้าย → H-L → F-L → Fl-H → H-H → สลับขวา → Fl-R → สลับซ้าย → F-H → สลับขวา → H-R → F-R"
      ),
      onCompleted = "ปริศนาคลี่คลาย ปลดล็อกสระฝน Rain Pool / Fountain ให้พร้อมใช้งาน",
      emergencySwarm = "ลากซอมบี้ให้เหลือตัวเดียว (หรือคลาน) ก่อนเริ่มขยับ Cube เพื่อไม่ให้เสียสมาธิ",
      checkBeforeNext = "สระน้ำ Rain Pool เปิดเปล่งแสงพร้อมกด",
      note = "[ต้องตรวจสอบ] สัญลักษณ์บนลูกบาศก์อาจแปรผันในแต่ละเกม ให้ยึดบล็อกที่พบในเกมจริง",
      category = StepCategory.FORGE_CUBE
    ),
    QuestStep(
      id = 14,
      stepNumberText = "[14]",
      title = "ชำระล้าง 4 วิหาร (4 Temples Purification)",
      area = "Dravakar, Nyxara, Caltheris, Veytharion",
      visualCue = "เสา Forge ทั้ง 3, ลำแสง Shadowsmith, ฟ้าผ่า",
      routeFrom = "หมุนเสา Nexus Forge ให้แสงพุ่งเข้าหัว Shadowsmith ของแต่ละวิหาร",
      instructions = listOf(
        "สูตรกลาง: Item → เผา Brazier → เอา Astral Flame → ยิงมอน Mist → ลากเข้า Titan Trap → ยืน Plate → ยิง Charged Blight สวน Beam → ฆ่าซอมบี้เติม Soul จนกำแพงแตก → ยิงหน้า Statue → สู้ Lockdown ฆ่า 4 Shadow Souls → ยิงหน้า Statue ซ้ำ → ดึง Purple Orb มาใต้หัววิหาร → ยิง Charged Blight ส่ง Orb ขึ้นหัว → ทำให้ฝนตก → รับ Astral Flame → ยิงจุดเรืองแสงบนหัววิหาร → เกิดปรากฏการณ์สายฟ้าฟาด (Lightning)!",
        "14A Dravakar: Hammer (Shatter Blast โล่ใน Inner) | Titan ฝั่ง Caltheris | Plate ยืน re-plate เกราะ | Beam เล็งผนังหลังขวา",
        "14B Nyxara: Ancient Scroll (Shadow Rift ใน Whispers) | Titan ฝั่ง Veytharion | Plate หมอบ | Beam เล็งล่างซ้ายใกล้หลุมลาวา",
        "14C Caltheris: Shimmering Thread (ดึง Purple Orb) | Titan ฝั่ง Caltheris | Plate ยืน ADS | Beam สังเกตรอยแตกทอง",
        "14D Veytharion: Woven Sash (กด Rain Pool รอฝนตก สลิงเก็บนอกขอบ) | Titan ฝั่ง Veytharion | Plate กระโดด | Beam ทางเดินซ้ายหลัง barrier"
      ),
      onCompleted = "สายฟ้าผ่าลงสู่วิหารครบทั้ง 4 แห่ง สัญลักษณ์ฟ้าสว่างวาบ",
      emergencySwarm = "ช่วง Lockdown ฆ่า 4 Shadow Souls ให้ใช้ Warden's Blight ยิงกวาดทันที",
      checkBeforeNext = "ต้องเห็นสายฟ้า Lightning ยืนยันครบทั้ง 4 วิหาร (Dravakar, Nyxara, Caltheris, Veytharion)",
      note = "ถ้าวงบอสไม่เปิด แสดงว่ายังมีอย่างน้อย 1 วิหารที่ฟ้ายังไม่ผ่า",
      category = StepCategory.TEMPLES
    ),
    QuestStep(
      id = 15,
      stepNumberText = "[15]",
      title = "เปิดประตูสู่บอส Nexus Core",
      area = "Nexus Core (ใต้ Pack-a-Punch)",
      visualCue = "วงกลมเรืองแสงสีน้ำเงินบนพื้น",
      routeFrom = "กลับสู่ Nexus Forge หลังทำวิหารครบ 4 แห่ง ลงชั้นใต้ดิน",
      instructions = listOf(
        "ลงไปที่ห้อง Nexus Core ใต้เครื่อง Pack-a-Punch",
        "ผู้เล่นทุกคนเดินไปยืนประจำตำแหน่งในวงกลมสีน้ำเงินของตนเอง",
        "กดปุ่ม Interact พร้อมกันเพื่อสั่ง 'Descend' ดำดิ่งลงสู่สนามรบบอส"
      ),
      onCompleted = "เคลื่อนย้ายผู้เล่นทุกคนลงสู่สนามประลองบอส Warden",
      emergencySwarm = "อย่าเพิ่งกดถ้าสมาชิกในทีมยังเตรียมตัวไม่เสร็จ ตรวจสอบกระสุนและเกราะให้เต็ม",
      checkBeforeNext = "ตรวจเช็กรายการอุปกรณ์ก่อนบอส (Step 16) ครบถ้วน",
      note = "ถ้าวงสีน้ำเงินไม่ปรากฏ แปลว่ามีวิหารอย่างน้อย 1 แห่งยังชำระล้างไม่เสร็จ",
      category = StepCategory.BOSS
    ),
    QuestStep(
      id = 16,
      stepNumberText = "[16]",
      title = "เช็กลิสต์ความพร้อมก่อนสู้บอส (Pre-Boss Checklist)",
      area = "Nexus Core / เตรียมตัว",
      visualCue = "เกราะ, กระสุน, อาวุธ PaP, Perk",
      routeFrom = "ก่อนก้าวเข้าสู่วงวาร์ปบอส",
      instructions = listOf(
        "[ ] ปืน Mammoth (หรือปืนยิงคริติคอล) อัปเกรด Pack-a-Punch ระดับ III",
        "[ ] อาวุธระดับความหายาก Legendary (สีทอง) ถ้าทำได้",
        "[ ] Warden's Blight อัปเกรด Pack-a-Punch ระดับ II หรือ III",
        "[ ] กระสุนเต็มทุกกระบอก พร้อมเงินสำรองซื้อกระสุนในสนาม",
        "[ ] เกราะ 3 ชั้นเต็ม 100% พร้อมแผ่นเกราะสำรองเต็มกระเป๋า",
        "[ ] Self-Revive Kit (สำคัญมากโดยเฉพาะเล่นคนเดียว)",
        "[ ] Perks สำคัญ: Jugger-Nog, Stamin-Up, Speed Cola, Deadshot Daiquiri, Quick Revive",
        "[ ] Field Upgrade ชาร์จพลังงานเต็มหลอด (Aether Shroud หรือ Frenzied Guard)"
      ),
      onCompleted = "พร้อมร้อยเปอร์เซ็นต์สำหรับการปะทะกับ Warden",
      emergencySwarm = "ฟาร์มเงินรอบ Nexus ซื้อของให้ครบก่อนลงบอส อย่ารีบร้อน",
      checkBeforeNext = "ทุกคนกดยืนยันความพร้อม",
      note = null,
      category = StepCategory.BOSS
    ),
    QuestStep(
      id = 17,
      stepNumberText = "[17]",
      title = "ศึกตัดสินกับบอส Warden",
      area = "สนามประลอง Warden Boss Arena",
      visualCue = "บอส Warden, Dread Skulls, Shadow Souls, Stingers, หางบอส",
      routeFrom = "ลงสู่ลานประลองผ่านวง Descend",
      instructions = listOf(
        "สูตรจำอาวุธ: Warden's Blight = ยิง Dread Skull / Shadow Soul / Stinger | Mammoth = เล็งหางและจุดอ่อน Weakpoint",
        "Phase 1: ใช้ Warden's Blight ยิง Dread Skull ให้แตก → บอสชะงัก ล้มเปิดจุดอ่อนที่หาง → สลับ Mammoth ยิงอัดหางเน้นๆ → ถอยออกมาวิ่งวน",
        "เมื่อ Warden เป็นอมตะ (Immune): หยุดยิงบอสทันที! มองหา Shadow Souls แล้วฆ่าให้เกลี้ยง → กลับมายิงกะโหลก Skull ใหม่",
        "Phase 2+: บอสปล่อยเหล็กใน Warden's Stingers: ลำดับความสำคัญ: ฆ่า Stingers ก่อน → ฆ่า Shadow Souls → Blight ยิงกะโหลก → Mammoth ยิงหาง",
        "การหลบ Warden พุ่งชน: ห้ามวิ่งถอยหลังตรงๆ เด็ดขาด! ให้ใช้สลิง Void Talon Grapple ข้ามหรือเปลี่ยนมุมทันที และระวังตกขอบเวที",
        "คุกขัง Prison / Clausura: เอาชีวิตรอดตามเวลา หรือรีบกำจัด Elite มอนสเตอร์ (ในโหมด Co-op ผู้เล่นอาจถูกแยกขัง)",
        "Phase สุดท้าย: อย่าประมาทเมื่อบอสล้มครั้งแรก ทำลูป Souls → Skull → Tail ซ้ำจนกระทั่งคัตซีนเริ่ม"
      ),
      onCompleted = "กำจัดบอส Warden ได้สำเร็จ!",
      emergencySwarm = "ถ้าเกราะแตก ให้กด Aether Shroud หนี หรือใช้ Frenzied Guard ซ่อมเกราะฉุกเฉิน",
      checkBeforeNext = "Warden สิ้นฤทธิ์ คัตซีนดำเนินเรื่องเริ่มเล่น",
      note = "อย่าฝืนยิง Warden ขณะขึ้นสถานะ Immune ให้รีบกำจัด Souls รอบสนามก่อน",
      category = StepCategory.BOSS
    ),
    QuestStep(
      id = 18,
      stepNumberText = "[18]",
      title = "เควสสำเร็จ (Main Quest Complete)",
      area = "Cutscene / สรุปภารกิจ",
      visualCue = "ฉากจบ Cutscene โบราณคดี Aether",
      routeFrom = "หลัง Warden ถูกกำจัด",
      instructions = listOf(
        "ชมฉากคัตซีนบทสรุปของ Rex Infernus",
        "รับฉายา ปลดล็อก Calling Card และความสำเร็จ Main Quest ประจำแมพ"
      ),
      onCompleted = "ภารกิจ Rex Infernus Live Main Quest สำเร็จลุล่วง 100%!",
      emergencySwarm = "ผ่อนคลายและฉลองชัยชนะกับทีม",
      checkBeforeNext = "บันทึกสถิติรอบการเล่น",
      note = null,
      category = StepCategory.BOSS
    )
  )

  val quickLocations: List<QuickLocationGuide> = listOf(
    QuickLocationGuide(
      id = "house",
      areaName = "Her House (บ้านเกิด)",
      lookFor = "โทรศัพท์ในบ้าน, หน้าต่างตรงข้าม Coda-9, ซากหลังคา, Portal ริมหน้าผา",
      whatToDo = "ช่วงต้น: รอโทรศัพท์ดัง กดรับ แล้วออกไปเข้า Portal | ช่วงกลาง: ยิงลูกบาส จำสัญลักษณ์ Exfil 4 จุด และยิง Blight เข้าหน้าต่างเก็บ Eye of the Forge",
      returnTo = "Nexus Forge ผ่าน Portal",
      ifSwarmed = "วิ่งวนรอบนอกตัวบ้าน ห้ามหนีเข้าห้องปิดตายมุมอับเด็ดขาด",
      keyItems = "โทรศัพท์, Portal, สัญลักษณ์ Exfil, Eye of the Forge"
    ),
    QuickLocationGuide(
      id = "nexus",
      areaName = "Nexus Forge (ศูนย์กลาง)",
      lookFor = "แท่นบูชา World Seed, ตู้ PaP, เสาสลิงสีเขียว, 3 เสาไฟชำระล้าง",
      whatToDo = "จุดไฟ 2 Brazier เปิด PaP | หยิบสลิง Void Claw | ซ่อมผนึก Seal 3 เสาและใยแมงมุมห้องใต้ดิน | หมุนลำแสงเสาเล็งเข้าปาก Shadowsmith ของ 4 วิหาร",
      returnTo = "เป็นฮับหลัก ถ้าหลงที่ไหนให้กลับมาที่นี่ก่อนเสมอ",
      ifSwarmed = "วิ่งลูปวงกลมรอบแท่น Pack-a-Punch มีพื้นที่กว้างที่สุดในแมพ",
      keyItems = "World Seed, Pack-a-Punch, Void Claw/Talon, Astral Flame"
    ),
    QuickLocationGuide(
      id = "dravakar",
      areaName = "Dravakar (วิหารค้อน)",
      lookFor = "ชั้น 2 เหนือประตูเข้า, ข้อความสีน้ำเงินบนผนัง, ทั่งตีเหล็ก Anvil, โล่ตามมุม",
      whatToDo = "สลิงขึ้นชั้นสองระเบิดผนังเก็บ Fracture | แก้ปริศนาประโยคฟ้ากดสวิตช์ | สลิงขึ้นทั่ง Anvil สร้าง Warden's Blight | ใช้ Shatter Blast ใส่โล่เอา Hammer",
      returnTo = "Nexus Forge",
      ifSwarmed = "สลิง Void Claw ดึงตัวเองขึ้นชั้นบนเพื่อพักหายใจและรีเกราะ",
      keyItems = "Fracture of Nyxara, Anvil, Blacksmith's Hammer, Titan Caltheris"
    ),
    QuickLocationGuide(
      id = "nyxara",
      areaName = "Nyxara (วิหารมิติอดีต)",
      lookFor = "แท่นเหนือกล่องกระสุน, ดวงตา Eye บนผนัง, คริสตัล 3 จุด, เถาวัลย์ Vine",
      whatToDo = "วาง Fracture บนผนัง | ยิง Astral Flame ใส่ตา หมุนคริสตัล 3 อัน | กระโดดลงหลุมให้เถาวัลย์ดูดพลังจนผลไม้ Fruit เกิด (Round 8+) แล้วกินไป Whispers",
      returnTo = "Nexus Forge",
      ifSwarmed = "หมุนคริสตัลทีละต้น อย่าแช่อยู่กับที่ ลากซอมบี้ไปห้องโถงกว้างก่อน",
      keyItems = "Crystal, Fruit, Ancient Scroll (Shadow Rift), Titan Veytharion"
    ),
    QuickLocationGuide(
      id = "whispers",
      areaName = "Whispers of the Past (ป่าแห่งอดีต)",
      lookFor = "ศพในถ้ำซ้าย/ขวา, ศพพิงหินกลางป่า, หลุมใต้พื้นบ้านพัง",
      whatToDo = "เก็บ 3 ชิ้นส่วนของ Warden's Blight: Bow (ในถ้ำ) + Stock (ข้างหินกลาง) + Handles (ใต้บ้าน) | ฆ่าซอมบี้ด้วย Shadow Rift จนได้ Ancient Scroll",
      returnTo = "Nyxara ผ่าน Portal ย้อนกลับ",
      ifSwarmed = "วิ่งวนรอบก้อนหินใหญ่กลางป่า อย่าวิ่งชิดขอบต้นไม้เพราะอาจติดมุม",
      keyItems = "Bow, Stock, Handles, Ancient Scroll"
    ),
    QuickLocationGuide(
      id = "caltheris",
      areaName = "Caltheris (วิหารด้ายทอง)",
      lookFor = "ลูกแก้วสีม่วง Purple Orb, Sharpshooter Floor Tiles 4 จุด, แผ่น Plate",
      whatToDo = "ดึง Purple Orb กด interact ซ้ำๆ จนได้ Shimmering Thread | ยืนบน Plate แล้วเล็งผ่านศูนย์เล็ง (ADS) จนแผ่นเปิด | เล็ง Beam ตามรอยแตกสีทอง",
      returnTo = "Nexus Forge",
      ifSwarmed = "ทำลูกแก้วทีละลูก อย่ายืนกดคาถ้ามอนเข้ามาใกล้",
      keyItems = "Purple Orb, Shimmering Thread, Lantern, Titan Caltheris"
    ),
    QuickLocationGuide(
      id = "veytharion",
      areaName = "Veytharion (วิหารลูกบาศก์ & ฝน)",
      lookFor = "Cube 4 ก้อน (Fire, Hand, Water, Flower), สระฝน Rain Pool, แผ่น Plate",
      whatToDo = "แก้ Cube puzzle เปิด Rain Pool | กดให้ฝนตกแล้วไปสลิงเก็บ Woven Sash ขอบเกาะ | กระโดดบน Plate จนเปิด | ยิง Beam ทางเดินซ้าย",
      returnTo = "Nexus Forge",
      ifSwarmed = "เหลือซอมบี้ 1 ตัวค่อยทำ Cube และระวังตกขอบเหวลอยฟ้า",
      keyItems = "4 Cubes, Rain Pool, Woven Sash, Titan Veytharion"
    ),
    QuickLocationGuide(
      id = "boss_arena",
      areaName = "Nexus Core & Warden Arena (สนามบอส)",
      lookFor = "วงสีน้ำเงินใต้ PaP (Descend), กะโหลก Dread Skull, หาง Warden, Stingers",
      whatToDo = "ทุกคนยืนวงฟ้าลงสู่สนาม | Blight ยิงกะโหลก Skull / Shadow Souls / Stingers | Mammoth กระหน่ำยิงหางคริติคอล | สลิงหนีตอนบอสพุ่ง",
      returnTo = "จบเควสเมื่อบอสตาย (Cutscene)",
      ifSwarmed = "ห้ามยืนแลกเด็ดขาด สลิง Grapple ข้ามไปอีกฝั่งของเวทีทันที",
      keyItems = "Warden's Blight, Mammoth PaP III, Aether Shroud, Self-Revive"
    )
  )

  val temples: List<TempleGuide> = listOf(
    TempleGuide(
      id = "dravakar",
      name = "Dravakar (วิหารช่างตีเหล็ก)",
      itemName = "Blacksmith's Hammer",
      itemMethod = "ใช้กระสุน Shatter Blast ยิงใส่โล่ตามมุมใน Dravakar Inner Sanctum จนเปิดออก แล้วเก็บค้อน",
      titanSide = "ใช้ Titan Trap ฝั่ง Caltheris",
      plateAction = "ยืนบนแผ่น Plate แล้วกดเปลี่ยนแผ่นเกราะ (Re-plate) ต่อเนื่องจนกระทั่งแผ่นเปิด",
      beamTarget = "เล็งลำแสงไปที่ผนังด้านหลังฝั่งขวา",
      checkSignal = "ฆ่าซอมบี้ 1 ตัว ต้องมองเห็นรอยแตกสีทองเปล่งประกาย",
      inspectionAlert = "จำนวนการ re-plate อาจไม่เท่ากันในแต่ละรอบ ให้ทำไปเรื่อยๆ จนแผ่นเปิดจริง"
    ),
    TempleGuide(
      id = "nyxara",
      name = "Nyxara (วิหารกาลเวลา)",
      itemName = "Ancient Scroll",
      itemMethod = "ใส่กระสุน Shadow Rift เข้าไปใน Whispers of the Past ฆ่าซอมบี้จนกระทั่ง Rift ทำงานและ Scroll ดรอป",
      titanSide = "ใช้ Titan Trap ฝั่ง Veytharion",
      plateAction = "หมอบ (Crouch/Prone) บนแผ่น Plate ค้างไว้จนแผ่นเปิด",
      beamTarget = "เล็งลำแสงลงด้านล่างซ้าย ใกล้กับหลุมลาวา",
      checkSignal = "ฆ่าซอมบี้ 1 ตัว ต้องมองเห็นรอยแตกสีทอง",
      inspectionAlert = "ระยะเวลาหมอบอาจต่างกันในแต่ละเกม ให้ดูว่าแผ่นเปิดแล้วจึงลุกขึ้น"
    ),
    TempleGuide(
      id = "caltheris",
      name = "Caltheris (วิหารด้ายถัก)",
      itemName = "Shimmering Thread",
      itemMethod = "ดึง Purple Orb กด Interact ซ้ำๆ จน Thread ดรอป (หรือยิงพื้นกระเบื้อง Sharpshooter 4 จุดเปิดห้องลับใช้ Lantern)",
      titanSide = "ใช้ Titan Trap ฝั่ง Caltheris",
      plateAction = "ยืนบนแผ่น Plate พร้อมเล็งผ่านศูนย์เล็ง (ADS) ค้างไว้จนแผ่นเปิด",
      beamTarget = "สังเกตรอยแตกสีทองบนผนังเป็นตัวนำทาง (อย่ายึดทิศเข็มทิศ)",
      checkSignal = "รอยแตกสีทองบนผนังปรากฏชัดเจน",
      inspectionAlert = "เวลาเล็ง ADS อาจแปรผันตามรอบเกม ให้มองแผ่นเปิดสมบูรณ์"
    ),
    TempleGuide(
      id = "veytharion",
      name = "Veytharion (วิหารฝนและสายลม)",
      itemName = "Woven Sash (ต้องทำตอนฝนตก)",
      itemMethod = "กด Rain Pool ให้ฝนตก เปลี่ยนรอบ แล้วไปสลิงเก็บที่ขอบเกาะ (Spira ฝั่ง Jugg / หลัง Mystery Box / Aranea ฝั่ง Widow)",
      titanSide = "ใช้ Titan Trap ฝั่ง Veytharion",
      plateAction = "กระโดด (Jump) ต่อเนื่องตรงกึ่งกลางแผ่น Plate จนกระทั่งแผ่นเปิดออก",
      beamTarget = "เล็งลำแสงไปที่ทางเดินซ้ายด้านหลังสิ่งกีดขวาง Zombie Barrier",
      checkSignal = "รอยแตกสีทองบนผนังทางเดินซ้าย",
      inspectionAlert = "ถ้าฝนหยุดก่อนเจอ ให้รอรอบถัดไปแล้วกด Rain Pool ใหม่อีกครั้ง"
    )
  )

  val cubeMoves: List<CubeMove> = listOf(
    CubeMove(1, "F H W Fl", "ตำแหน่งเริ่มต้น: Fire, Hand, Water, Flower"),
    CubeMove(2, "F-H", "ย้าย Fire เข้าช่องแนวนอน (Horizontal)"),
    CubeMove(3, "H-H", "ย้าย Hand เข้าช่องแนวนอน"),
    CubeMove(4, "สลับขวา", "สลับตำแหน่งแถบรางด้านขวา"),
    CubeMove(5, "F-R", "เลื่อน Fire ลงช่องตั้งฝั่งขวา (Right Vertical)"),
    CubeMove(6, "สลับซ้าย", "สลับตำแหน่งแถบรางด้านซ้าย"),
    CubeMove(7, "W-H", "ย้าย Water เข้าช่องแนวนอน"),
    CubeMove(8, "สลับขวา", "สลับตำแหน่งรางด้านขวา"),
    CubeMove(9, "H-R", "เลื่อน Hand ลงช่องตั้งฝั่งขวา"),
    CubeMove(10, "W-R", "เลื่อน Water ลงช่องตั้งฝั่งขวา"),
    CubeMove(11, "F-H", "นำ Fire ขึ้นสู่ช่องแนวนอน"),
    CubeMove(12, "H-H", "นำ Hand ขึ้นสู่ช่องแนวนอน"),
    CubeMove(13, "สลับซ้าย", "สลับรางด้านซ้าย"),
    CubeMove(14, "H-L", "เลื่อน Hand ลงช่องตั้งฝั่งซ้าย (Left Vertical)"),
    CubeMove(15, "F-L", "เลื่อน Fire ลงช่องตั้งฝั่งซ้าย"),
    CubeMove(16, "Fl-H", "ย้าย Flower เข้าช่องแนวนอน"),
    CubeMove(17, "H-H", "ย้าย Hand เข้าช่องแนวนอน"),
    CubeMove(18, "สลับขวา", "สลับรางด้านขวา"),
    CubeMove(19, "Fl-R", "เลื่อน Flower ลงช่องตั้งฝั่งขวา"),
    CubeMove(20, "สลับซ้าย", "สลับรางด้านซ้าย"),
    CubeMove(21, "F-H", "นำ Fire ขึ้นสู่ช่องแนวนอน"),
    CubeMove(22, "สลับขวา", "สลับรางด้านขวา"),
    CubeMove(23, "H-R", "เลื่อน Hand ลงช่องตั้งฝั่งขวา"),
    CubeMove(24, "F-R", "เลื่อน Fire ลงช่องตั้งฝั่งขวา → สำเร็จ! Rain Pool เปิดใช้งาน")
  )

  val speedFlowNodes = listOf(
    "HER HOUSE" to "รับโทรศัพท์บ้าน → ข้าม Portal สู่ Nexus",
    "NEXUS FORGE" to "วาง World Seed → ล่า 2 Dread Skull & จุด 2 Usurped Flame → เปิด PaP",
    "VOID CLAW" to "หยิบ Tactical สลิง Void Claw จากเสาเขียวรอบ PaP",
    "DRAVAKAR" to "สลิงขึ้นชั้นสอง ระเบิดผนังเก็บ Fracture of Nyxara",
    "NYXARA" to "วาง Fracture เหนือตู้กระสุน",
    "NEXUS FORGE" to "ดึง Purple Orb เข้ากลาง → Surge Nexus → เก็บ Astral Flame",
    "NYXARA SANCTUM" to "ยิง Astral Flame ใส่ตา → หมุน 3 คริสตัล → กินผลไม้ Fruit (Round 8+)",
    "WHISPERS OF PAST" to "เก็บ 3 ชิ้นส่วน: Bow + Stock + Handles (ไม่ใช่ค้อน/ด้าย/ม้วนคัมภีร์)",
    "DRAVAKAR ANVIL" to "แก้ปริศนาอักษรฟ้าผนัง → ตีทั่ง Anvil → สร้าง Warden's Blight",
    "UPGRADE TALON" to "ยิงลูกบาส → จดสัญลักษณ์บ้าน 1-2-3-4 → Exfil → ยิง Blight ใส่หน้าต่างบ้าน → ได้ Void Talon",
    "REPAIR FORGE" to "ทำลาย 3 Seal บนเสา + Seal ผนังห้องใต้ PaP + ระเบิดใยแมงมุม + โยก Lever",
    "VEYTHARION CUBE" to "แก้ปริศนา 4 ลูกบาศก์ (ห้ามไฟแตะน้ำ/ดอกไม้) → เปิด Rain Pool",
    "4 TEMPLES PURIFY" to "หมุนเสาให้แสงเข้าปาก Shadowsmith → ทำ Item + Titan + Plate + Beam + 4 Souls → ฟ้าผ่าครบ 4 แห่ง!",
    "NEXUS CORE" to "ทุกคนยืนวงสีน้ำเงินใต้ PaP → กด Descend สู่สนามประลองบอส",
    "WARDEN BOSS" to "Blight ยิง Skull/Souls/Stingers | Mammoth ยิงหาง Weakpoint → ปราบ Warden จบ Main Quest!"
  )

  val loadoutItems: List<LoadoutItem> = listOf(
    LoadoutItem("mammoth", "ปืนหลัก: Mammoth", "อาวุธ", "DPS ยิงจุดอ่อนหางบอส Warden ได้เร็วที่สุด ต้อง PaP III ก่อนลงบอส", true),
    LoadoutItem("warden_blight", "อาวุธมหัศจรรย์: Warden's Blight", "อาวุธ", "สำหรับยิง Dread Skull, Shadow Souls และ Stingers ต้อง PaP II-III", true),
    LoadoutItem("secondary_dps", "ปืนเสริม: High-DPS Rifle/SMG", "อาวุธ", "ปืนกระสุนธรรมดาคุมรีคอยล์ง่าย ไว้เคลียร์ฝูงซอมบี้ทั่วไป", false),
    LoadoutItem("juggernog", "Perk: Jugger-Nog", "สิทธิพิเศษ (Perks)", "เพิ่มพลังชีวิตสูงสุด ลดโอกาสโดนตบตายในเสี้ยววินาที", true),
    LoadoutItem("staminup", "Perk: Stamin-Up", "สิทธิพิเศษ (Perks)", "เพิ่มความเร็วในการวิ่ง ช่วยให้ลากฝูงและสปีดหนีบอสได้ทัน", true),
    LoadoutItem("speedcola", "Perk: Speed Cola", "สิทธิพิเศษ (Perks)", "รีโหลดกระสุนรวดเร็วมาก ช่วยชีวิตเวลากระสุนหมดกลางดง", true),
    LoadoutItem("deadshot", "Perk: Deadshot Daiquiri", "สิทธิพิเศษ (Perks)", "ล็อกเป้าส่วนหัวและจุดอ่อนอัตโนมัติ เพิ่มดาเมจคริติคอล", true),
    LoadoutItem("quickrevive", "Perk: Quick Revive", "สิทธิพิเศษ (Perks)", "ฟื้นฟูพลังชีวิตเร็วขึ้น และจำเป็นอย่างยิ่งสำหรับผู้เล่นเดี่ยว (Solo)", true),
    LoadoutItem("deathperception", "Perk: Death Perception", "สิทธิพิเศษ (Perks)", "มองเห็นซอมบี้ทะลุกำแพงและขอบประตู ป้องกันโดนซุ่มตบ", false),
    LoadoutItem("aether_shroud", "Field Upgrade: Aether Shroud", "อุปกรณ์พิเศษ", "ล่องหน 5 วินาที ดีที่สุดสำหรับยืนทำปริศนาหรือหนีวิกฤต", true),
    LoadoutItem("frenzied_guard", "Field Upgrade: Frenzied Guard", "อุปกรณ์พิเศษ", "ซ่อมเกราะเต็มทันทีและลดความเร็วซอมบี้ เหมาะสำหรับสายแทงก์เกราะแตกบ่อย", false),
    LoadoutItem("self_revive", "Self-Revive Kit", "เสบียงฉุกเฉิน", "ชุบชีวิตตัวเองเมื่อล้ม ห้ามลืมซื้อก่อนลงสนามบอส", true),
    LoadoutItem("shatter_blast", "Ammo Mod: Shatter Blast", "ม็อดกระสุน", "จำเป็นสำหรับระเบิดโล่ใน Dravakar เพื่อเก็บ Blacksmith's Hammer", true),
    LoadoutItem("shadow_rift", "Ammo Mod: Shadow Rift", "ม็อดกระสุน", "จำเป็นสำหรับยิงมอนสเตอร์ใน Whispers เพื่อดรอป Ancient Scroll", true),
    LoadoutItem("max_armor", "เกราะ 3 ชั้นเต็ม 100%", "การป้องกัน", "ซื้อเกราะระดับ 3 ให้เต็ม พร้อมพกพาแผ่นสำรองเต็มช่อง", true)
  )
}

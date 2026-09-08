package com.example.data

enum class StepCategory(val title: String) {
  ALL("ทั้งหมด"),
  EARLY("ช่วงแรก (บ้าน & PaP)"),
  WEAPON("สร้างอาวุธ & อัปเกรด"),
  FORGE_CUBE("ซ่อม Forge & Cube"),
  TEMPLES("ชำระ 4 วิหาร"),
  BOSS("บอส Warden")
}

data class QuestStep(
  val id: Int,
  val stepNumberText: String,
  val title: String,
  val area: String,
  val visualCue: String,
  val routeFrom: String,
  val instructions: List<String>,
  val onCompleted: String,
  val emergencySwarm: String,
  val checkBeforeNext: String,
  val note: String? = null,
  val category: StepCategory
)

data class QuickLocationGuide(
  val id: String,
  val areaName: String,
  val lookFor: String,
  val whatToDo: String,
  val returnTo: String,
  val ifSwarmed: String,
  val keyItems: String
)

data class TempleGuide(
  val id: String,
  val name: String,
  val itemName: String,
  val itemMethod: String,
  val titanSide: String,
  val plateAction: String,
  val beamTarget: String,
  val checkSignal: String,
  val inspectionAlert: String
)

data class CubeMove(
  val stepNumber: Int,
  val code: String,
  val description: String,
  val ruleCheck: String = "ห้าม Fire โดน Water หรือ Flower"
)

data class LoadoutItem(
  val id: String,
  val name: String,
  val category: String,
  val detail: String,
  val isBossEssential: Boolean = false
)

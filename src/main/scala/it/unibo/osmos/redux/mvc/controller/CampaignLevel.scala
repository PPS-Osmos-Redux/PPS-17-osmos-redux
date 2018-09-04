package it.unibo.osmos.redux.mvc.controller

/**
  * Campaign level
  * @param levelInfo level info
  * @param levelStat level statistics
  */
case class CampaignLevel(levelInfo: LevelInfo, var levelStat: CampaignLevelStat)

/**
  * Statistics of a campaign level
  * @param defeats number of defeats
  * @param victories number of victories
  */

case class CampaignLevelStat(var defeats:Int, var victories: Int)

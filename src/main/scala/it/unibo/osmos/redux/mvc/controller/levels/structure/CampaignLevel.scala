package it.unibo.osmos.redux.mvc.controller.levels.structure

/** Campaign level
  *
  * @param levelInfo levelInfo
  * @param levelStat CampaignLevelStat
  */
case class CampaignLevel(levelInfo: LevelInfo, var levelStat: CampaignLevelStat = CampaignLevelStat())

/** Statistics of a campaign level
  *
  * @param defeats   number of defeats default 0
  * @param victories number of victories default 0
  */
case class CampaignLevelStat(var defeats: Int = 0, var victories: Int = 0)
